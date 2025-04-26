import React, { useState, useEffect, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";
import { obtenerReservaPorId, obtenerReservasPorCliente } from "../Services/ReservaService";
import { crearComprobante } from "../Services/ComprobanteService";

const FormularioComprobante = () => {
  const { id } = useParams(); // Se espera la ruta /crear-comprobante/:id
  const navigate = useNavigate();

  // Estado para la reserva real obtenida del backend
  const [reserva, setReserva] = useState(null);

  // Estado para almacenar los datos del comprobante (basados en la reserva y descuentos)
  const [comprobanteData, setComprobanteData] = useState({
    idReserva: "",
    fechaEmision: new Date().toISOString().split("T")[0],
    fechaReserva: "",
    horaInicio: "",
    cantidadPersonas: 0,
    numeroVueltas: 0,
    duracionTotal: 0,
    clienteResponsable: "",
    precioTotal: 0, // Precio final descontado (con IVA, que ya viene en la reserva)
    totalConIva: 0, // Igual al precio final descontado, ya que se considera que el precio viene con IVA
  });

  // Estados para los distintos montos de descuento en moneda
  const [descuentoPersonasAmount, setDescuentoPersonasAmount] = useState(0);
  const [descuentoFrecuenciaAmount, setDescuentoFrecuenciaAmount] = useState(0);
  const [descuentoCumpleAmount, setDescuentoCumpleAmount] = useState(0);
  const [totalDescuentoAmount, setTotalDescuentoAmount] = useState(0);

  // Estado para los correos de participantes (editable)
  const [correosClientes, setCorreosClientes] = useState([]);

  useEffect(() => {
    obtenerReservaPorId(id)
      .then((response) => {
        const data = response.data;
        // Suponemos que data.precioTotal es el precio final con IVA (como se muestra en la reserva)
        const finalOriginal = data.precioTotal;

        // -----------------------
        // Descuento por número de personas
        let descuentoPercentPersonas = 0;
        if (data.cantidadPersonas >= 3 && data.cantidadPersonas <= 5) {
          descuentoPercentPersonas = 0.10;
        } else if (data.cantidadPersonas >= 6 && data.cantidadPersonas <= 10) {
          descuentoPercentPersonas = 0.20;
        } else if (data.cantidadPersonas >= 11 && data.cantidadPersonas <= 15) {
          descuentoPercentPersonas = 0.30;
        }
        const discountAmountPersonas = finalOriginal * descuentoPercentPersonas;

        // -----------------------
        // Descuento por cliente frecuente
        // Ahora, en lugar de usar data.clienteResponsable.cantidadReservas,
        // se llama a obtenerReservasPorCliente para contar todas las reservas del cliente.
        // Se asume que data.clienteResponsable.id está disponible.
        // Llamada para obtener todas las reservas del cliente
        obtenerReservasPorCliente(data.clienteResponsable.id)
            .then((resp) => {
                const reservasCliente = resp.data; // Array de reservas del cliente
                const cantidadReservas = reservasCliente.length;
                console.log("Cantidad de reservas del cliente:", cantidadReservas);

                // Forzamos: si el cliente tiene menos de 2 reservas, no se aplica descuento
                let descuentoPercentFrecuencia = 0;
                if (cantidadReservas < 2) {  
                    descuentoPercentFrecuencia = 0;
                } else if (cantidadReservas >= 2 && cantidadReservas < 5) {  
                    descuentoPercentFrecuencia = 0.10;  
                } else if (cantidadReservas >= 5 && cantidadReservas < 7) {  
                    descuentoPercentFrecuencia = 0.20;  
                } else if (cantidadReservas >= 7) {  
                    descuentoPercentFrecuencia = 0.30;  
                }
            onsole.log("Factor de descuento por cliente frecuente:", descuentoPercentFrecuencia);
            const discountAmountFrecuencia = finalOriginal * descuentoPercentFrecuencia;

            // -----------------------
            // Descuento por cumpleaños (día especial)
            // Se asume que si data.diaEspecial es true, se aplica la promoción:
            // - Para grupos de 3 a 5: 50% de descuento de 1 cuota individual.
            // - Para grupos de 6 a 10: 50% de descuento de 2 cuotas individuales.
            let discountAmountCumple = 0;
            if (data.diaEspecial === true) {
              if (data.cantidadPersonas >= 3 && data.cantidadPersonas <= 5) {
                discountAmountCumple = (finalOriginal / data.cantidadPersonas) * 0.5;
              } else if (data.cantidadPersonas >= 6 && data.cantidadPersonas <= 10) {
                discountAmountCumple = (finalOriginal / data.cantidadPersonas) * 0.5 * 2;
              }
            }

            // -----------------------
            // Total descuento en valor monetario
            const totalDiscountAmount = discountAmountPersonas + discountAmountFrecuencia + discountAmountCumple;

            // Precio final descontado (con IVA) = precio original (con IVA) − total descuentos
            const finalPriceDiscounted = finalOriginal - totalDiscountAmount;

            // Actualizar el estado con los datos calculados
            setReserva(data);
            setComprobanteData({
              idReserva: data.idReserva,
              fechaEmision: new Date().toISOString().split("T")[0],
              fechaReserva: data.fechaReserva,
              horaInicio: data.horaInicio,
              cantidadPersonas: data.cantidadPersonas,
              numeroVueltas: data.numeroVueltas,
              duracionTotal: data.duracionTotal,
              clienteResponsable: data.clienteResponsable.nombre,
              precioTotal: finalPriceDiscounted,  // Este es el precio final (con IVA) ya descontado.
              totalConIva: finalPriceDiscounted,   // Se muestra con IVA.
            });
            setDescuentoPersonasAmount(discountAmountPersonas);
            setDescuentoFrecuenciaAmount(discountAmountFrecuencia);
            setDescuentoCumpleAmount(discountAmountCumple);
            setTotalDescuentoAmount(totalDiscountAmount);

            // Inicializar los campos de correo según la cantidad de participantes
            setCorreosClientes(Array.from({ length: data.cantidadPersonas }, () => ""));
          })
          .catch((error) => {
            console.error("Error al obtener reservas del cliente:", error);
          });
      })
      .catch((error) => {
        console.error("Error al obtener la reserva:", error);
      });
  }, [id]);

  // Para mostrar el precio sin IVA, se divide el precio final (con IVA) entre 1.19.
  const totalSinIva = useMemo(() => {
    return (comprobanteData.totalConIva / 1.19).toFixed(2);
  }, [comprobanteData.totalConIva]);

  // Handler para actualizar un correo individual
  const handleCorreoChange = (index, value) => {
    const nuevosCorreos = [...correosClientes];
    nuevosCorreos[index] = value;
    setCorreosClientes(nuevosCorreos);
  };

  // Función para generar el PDF, incluyendo el desglose de descuentos.
  const generarPDFBlob = () => {
    console.log("Generando PDF Blob...");
    const doc = new jsPDF();

    // Título
    doc.setFont("helvetica", "bold");
    doc.setFontSize(16);
    doc.text("Comprobante de Pago", 70, 20);

    // Datos generales
    doc.setFont("helvetica", "normal");
    doc.setFontSize(12);
    doc.rect(10, 30, 190, 110);
    doc.text(`ID Reserva: ${comprobanteData.idReserva}`, 20, 40);
    doc.text(`Fecha de Emisión: ${comprobanteData.fechaEmision}`, 20, 50);
    doc.text(`Fecha de Reserva: ${comprobanteData.fechaReserva}`, 20, 60);
    doc.text(`Hora de Inicio: ${comprobanteData.horaInicio}`, 20, 70);
    doc.text(`Cantidad de Personas: ${comprobanteData.cantidadPersonas}`, 20, 80);
    doc.text(`Número de Vueltas: ${comprobanteData.numeroVueltas}`, 20, 90);
    doc.text(`Duración Total: ${comprobanteData.duracionTotal} min`, 20, 100);
    doc.text(`Cliente Responsable: ${comprobanteData.clienteResponsable}`, 20, 110);
    doc.text(`Precio Final (con IVA): $${comprobanteData.totalConIva.toFixed(2)}`, 20, 120);
    doc.text(`Precio Final sin IVA: $${totalSinIva}`, 20, 130);

    // Sección de descuentos
    let posY = 140;
    doc.rect(10, posY, 190, 50);
    posY += 10;
    doc.text("Detalle de Descuentos:", 20, posY);
    posY += 10;
    doc.text(
      `Descuento por número de personas: -$${descuentoPersonasAmount.toFixed(2)}`,
      20,
      posY
    );
    posY += 10;
    doc.text(
      `Descuento por cliente frecuente: -$${descuentoFrecuenciaAmount.toFixed(2)}`,
      20,
      posY
    );
    posY += 10;
    doc.text(
      `Descuento por cumpleaños: -$${descuentoCumpleAmount.toFixed(2)}`,
      20,
      posY
    );
    posY += 10;
    doc.text(`Total Descuento: -$${totalDescuentoAmount.toFixed(2)}`, 20, posY);

    // Sección de correos de participantes
    posY += 20;
    doc.rect(10, posY, 190, 40);
    doc.text("Correos de Participantes:", 20, posY + 10);
    correosClientes.forEach((correo, index) => {
      doc.text(`${index + 1}. ${correo}`, 20, posY + 20 + index * 10);
    });

    const blob = doc.output("blob");
    console.log("PDF Blob generado:", blob);
    return blob;
  };

  // Handler para enviar el comprobante al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Iniciando envío del comprobante...");

    const pdfBlob = generarPDFBlob();
    console.log("PDF Blob obtenido:", pdfBlob);

    const formData = new FormData();
    formData.append("idReserva", comprobanteData.idReserva);
    formData.append("fechaEmision", comprobanteData.fechaEmision);
    formData.append("totalConIva", comprobanteData.totalConIva);
    formData.append("archivoPdf", pdfBlob, `comprobante_${comprobanteData.idReserva}.pdf`);
    formData.append("correosClientes", correosClientes.join(","));

    console.log("FormData preparado:");
    for (let [key, value] of formData.entries()) {
      console.log(`${key}: ${value}`);
    }

    try {
      console.log("Enviando comprobante al backend...");
      const response = await crearComprobante(formData);
      console.log("Respuesta del backend:", response);
      alert("Pago realizado con éxito. Se ha generado y enviado el comprobante.");
      navigate("/comprobantes");
    } catch (error) {
      console.error("Error al enviar comprobante:", error);
      alert("Error al enviar comprobante. Revisa la consola.");
    }
  };

  if (!reserva) {
    return <div>Cargando datos de la reserva...</div>;
  }

  return (
    <div className="container mt-4">
      <h2>Crear Comprobante de Pago</h2>
      <form onSubmit={handleSubmit}>
        {/* Mostrar datos de la reserva/comprobante */}
        <div className="mb-3">
          <label>ID Reserva</label>
          <input type="text" className="form-control" value={comprobanteData.idReserva} readOnly />
        </div>
        <div className="mb-3">
          <label>Fecha de Emisión</label>
          <input
            type="date"
            className="form-control"
            value={comprobanteData.fechaEmision}
            onChange={(e) =>
              setComprobanteData({ ...comprobanteData, fechaEmision: e.target.value })
            }
          />
        </div>
        <div className="mb-3">
          <label>Fecha de Reserva</label>
          <input type="date" className="form-control" value={comprobanteData.fechaReserva} readOnly />
        </div>
        <div className="mb-3">
          <label>Hora de Inicio</label>
          <input type="text" className="form-control" value={comprobanteData.horaInicio} readOnly />
        </div>
        <div className="mb-3">
          <label>Cantidad de Personas</label>
          <input type="number" className="form-control" value={comprobanteData.cantidadPersonas} readOnly />
        </div>
        <div className="mb-3">
          <label>Número de Vueltas</label>
          <input type="text" className="form-control" value={comprobanteData.numeroVueltas} readOnly />
        </div>
        <div className="mb-3">
          <label>Duración Total (min)</label>
          <input type="number" className="form-control" value={comprobanteData.duracionTotal} readOnly />
        </div>
        <div className="mb-3">
          <label>Cliente Responsable</label>
          <input type="text" className="form-control" value={comprobanteData.clienteResponsable} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio Final (con IVA)</label>
          <input
            type="number"
            className="form-control"
            value={comprobanteData.totalConIva.toFixed(2)}
            readOnly
          />
        </div>
        <div className="mb-3">
          <label>Precio Final sin IVA</label>
          <input type="number" className="form-control" value={totalSinIva} readOnly />
        </div>

        {/* Mostrar en pantalla el detalle de descuentos */}
        <h4>Descuentos Aplicados</h4>
        <p>Descuento por número de personas: -$ {descuentoPersonasAmount.toFixed(2)}</p>
        <p>Descuento por cliente frecuente: -$ {descuentoFrecuenciaAmount.toFixed(2)}</p>
        <p>Descuento por cumpleaños: -$ {descuentoCumpleAmount.toFixed(2)}</p>
        <p>Total Descuento: -$ {totalDescuentoAmount.toFixed(2)}</p>

        {/* Campos editables para los correos de los participantes */}
        <h4>Correos de Participantes</h4>
        {correosClientes.map((correo, index) => (
          <div className="mb-3" key={index}>
            <label>Correo Participante {index + 1}</label>
            <input
              type="email"
              className="form-control"
              value={correo}
              onChange={(e) => handleCorreoChange(index, e.target.value)}
              placeholder="Ingrese el correo"
            />
          </div>
        ))}

        <button type="submit" className="btn btn-success">
          Generar y Enviar Comprobante
        </button>
      </form>
    </div>
  );
};

export default FormularioComprobante;

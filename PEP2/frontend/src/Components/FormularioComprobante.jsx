import React, { useState, useEffect, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";
// Importamos el endpoint general para reservar (ya que el de cliente no funciona)
import { obtenerReservaPorId, obtenerReservas } from "../Services/ReservaService";
import ReservasCliente from "../Components/ReservasCliente";
import { crearComprobante } from "../Services/ComprobanteService";

const FormularioComprobante = () => {
  const { id } = useParams(); // Se espera la ruta /crear-comprobante/:id
  const navigate = useNavigate();

  // Estado para la reserva actual
  const [reserva, setReserva] = useState(null);

  // Estado para los datos del comprobante
  const [comprobanteData, setComprobanteData] = useState({
    idReserva: "",
    fechaEmision: new Date().toISOString().split("T")[0],
    fechaReserva: "",
    horaInicio: "",
    cantidadPersonas: 0,
    numeroVueltas: 0,
    duracionTotal: 0,
    clienteResponsable: "",
    precioTotal: 0, // Precio final con IVA ya descontado
    totalConIva: 0,
  });

  // Estados para los distintos montos de descuento
  const [descuentoPersonasAmount, setDescuentoPersonasAmount] = useState(0);
  const [descuentoFrecuenciaAmount, setDescuentoFrecuenciaAmount] = useState(0);
  const [descuentoCumpleAmount, setDescuentoCumpleAmount] = useState(0);
  const [totalDescuentoAmount, setTotalDescuentoAmount] = useState(0);

  // Estado para los correos (para participantes) y para sus errores
  const [correosClientes, setCorreosClientes] = useState([]);
  const [emailErrors, setEmailErrors] = useState([]);

  // Regex que valida que el correo tenga el formato básico sin espacios
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  useEffect(() => {
    // Primero, obtenemos la reserva por id
    obtenerReservaPorId(id)
      .then((response) => {
        const data = response.data;
        const finalOriginal = data.precioTotal; // Supuesto: precioFinal con IVA

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
        setDescuentoPersonasAmount(discountAmountPersonas);

        // -----------------------
        // Descuento por cumpleaños (día especial)
        let discountAmountCumple = 0;
        if (data.diaEspecial === true) {
          if (data.cantidadPersonas >= 3 && data.cantidadPersonas <= 5) {
            discountAmountCumple = (finalOriginal / data.cantidadPersonas) * 0.5;
          } else if (data.cantidadPersonas >= 6 && data.cantidadPersonas <= 10) {
            discountAmountCumple = (finalOriginal / data.cantidadPersonas) * 0.5 * 2;
          }
        }
        setDescuentoCumpleAmount(discountAmountCumple);

        // -----------------------
        // Descuento por cliente frecuente:
        // Dado que el endpoint GET /reservas/cliente/:id no funciona,
        // usamos obtenerReservas() y filtramos manualmente.
        obtenerReservas()
          .then((resp) => {
            const allReservas = Array.isArray(resp.data) ? resp.data : [];
            // Filtramos las reservas cuyo clienteResponsable.idCliente coincida
            const reservasDelCliente = allReservas.filter((r) => {
              console.log(
                "Comparando reserva cliente id:",
                r.clienteResponsable?.idCliente,
                "con prop clienteId:",
                data.clienteResponsable.idCliente
              );
              return (
                r.clienteResponsable &&
                Number(r.clienteResponsable.idCliente) === Number(data.clienteResponsable.idCliente)
              );
            });
            const cantidadReservas = reservasDelCliente.length;
            console.log(
              "Cantidad de reservas filtradas para clienteId",
              data.clienteResponsable.idCliente,
              ":",
              cantidadReservas
            );

            // Calcula el porcentaje según la tabla:
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
            console.log("Factor de descuento por cliente frecuente:", descuentoPercentFrecuencia);
            const discountAmountFrecuencia = finalOriginal * descuentoPercentFrecuencia;
            console.log("Monto de descuento de frecuencia calculado:", discountAmountFrecuencia);
            setDescuentoFrecuenciaAmount(discountAmountFrecuencia);

            // -----------------------
            // Sumar todos los descuentos y actualizar el precio final
            const totalDiscountAmount = discountAmountPersonas + discountAmountFrecuencia + discountAmountCumple;
            setTotalDescuentoAmount(totalDiscountAmount);
            const finalPriceDiscounted = finalOriginal - totalDiscountAmount;
            // Actualizamos los datos del comprobante
            setComprobanteData({
              idReserva: data.idReserva,
              fechaEmision: new Date().toISOString().split("T")[0],
              fechaReserva: data.fechaReserva,
              horaInicio: data.horaInicio,
              cantidadPersonas: data.cantidadPersonas,
              numeroVueltas: data.numeroVueltas,
              duracionTotal: data.duracionTotal,
              clienteResponsable: data.clienteResponsable.nombre,
              precioTotal: finalPriceDiscounted,
              totalConIva: finalPriceDiscounted,
            });
            // Se guarda la reserva y se inicializan los correos según la cantidad
            setReserva(data);
            const initialCorreos = Array.from({ length: data.cantidadPersonas }, () => "");
            setCorreosClientes(initialCorreos);
            setEmailErrors(Array.from({ length: data.cantidadPersonas }, () => ""));
          })
          .catch((error) => {
            console.error("Error al obtener reservas (frecuencia):", error);
          });
      })
      .catch((error) => {
        console.error("Error al obtener la reserva:", error);
      });
  }, [id]);

  // Calcula el precio sin IVA usando useMemo
  const totalSinIva = useMemo(() => {
    return (comprobanteData.totalConIva / 1.19).toFixed(2);
  }, [comprobanteData.totalConIva]);

  // Función para manejar el cambio de correo y validar el formato.
  const handleCorreoChange = (index, value) => {
    const trimmedValue = value.replace(/\s/g, "");
    let errorMessage = "";
    if (!emailRegex.test(trimmedValue)) {
      errorMessage = "El correo es inválido. Ingrese un correo con el formato x@x.xx";
    }
    setEmailErrors((prevErrors) => {
      const newErrors = [...prevErrors];
      newErrors[index] = errorMessage;
      return newErrors;
    });
    setCorreosClientes((prevCorreos) => {
      const nuevosCorreos = [...prevCorreos];
      nuevosCorreos[index] = trimmedValue;
      return nuevosCorreos;
    });
  };

  // Función para generar el PDF Blob
  const generarPDFBlob = () => {
    console.log("Generando PDF Blob...");
    const doc = new jsPDF();

    // Título y datos generales
    doc.setFont("helvetica", "bold");
    doc.setFontSize(16);
    doc.text("Comprobante de Pago", 70, 20);
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
    doc.text(`Descuento por número de personas: -$${descuentoPersonasAmount.toFixed(2)}`, 20, posY);
    posY += 10;
    doc.text(`Descuento por cliente frecuente: -$${descuentoFrecuenciaAmount.toFixed(2)}`, 20, posY);
    posY += 10;
    doc.text(`Descuento por cumpleaños: -$${descuentoCumpleAmount.toFixed(2)}`, 20, posY);
    posY += 10;
    doc.text(`Total Descuento: -$${totalDescuentoAmount.toFixed(2)}`, 20, posY);

    // Correos de participantes
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

  // Handler para enviar comprobante
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

  // Mientras no se cargue la reserva, mostramos un loader
  if (!reserva) {
    return <div>Cargando datos de la reserva...</div>;
  }

  return (
    <div className="container mt-4">
      <h2>Crear Comprobante de Pago</h2>
      <form onSubmit={handleSubmit}>
        {/* Datos del Comprobante */}
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
          <input type="number" className="form-control" value={comprobanteData.totalConIva.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio Final sin IVA</label>
          <input type="number" className="form-control" value={totalSinIva} readOnly />
        </div>

        <h4>Descuentos Aplicados</h4>
        <p>Descuento por número de personas: -${descuentoPersonasAmount.toFixed(2)}</p>
        <p>Descuento por cliente frecuente: -${descuentoFrecuenciaAmount.toFixed(2)}</p>
        <p>Descuento por cumpleaños: -${descuentoCumpleAmount.toFixed(2)}</p>
        <p>Total Descuento: -${totalDescuentoAmount.toFixed(2)}</p>

        {reserva.clienteResponsable && (
          <div className="mb-3">
            <h4>Reservas del Cliente</h4>
            <ReservasCliente clienteId={reserva.clienteResponsable.idCliente} />
          </div>
        )}

        <h4>Correos de Participantes</h4>
        {correosClientes.map((correo, index) => (
          <div className="mb-3" key={index}>
            <label>Correo Participante {index + 1}</label>
            <input
              type="email"
              className={`form-control ${emailErrors[index] ? "is-invalid" : ""}`}
              value={correo}
              onChange={(e) => handleCorreoChange(index, e.target.value)}
              placeholder="Ingrese el correo"
            />
            {emailErrors[index] && (
              <div className="invalid-feedback">{emailErrors[index]}</div>
            )}
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

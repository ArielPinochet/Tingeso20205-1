import React, { useState, useEffect, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";
import axios from "axios";
import { crearComprobante } from "../Services/ComprobanteService";

const FormularioComprobante = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // Estados para los datos
  const [reserva, setReserva] = useState(null);
  const [tarifa, setTarifa] = useState(null);
  const [descuento, setDescuento] = useState(null);

  // Estado para los correos y errores
  const [correosClientes, setCorreosClientes] = useState([]);
  const [emailErrors, setEmailErrors] = useState([]);

  // Regex para validar correo
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Cargar datos al montar
  useEffect(() => {
    // 1. Obtener reserva
    axios.get(`http://localhost:8080/app/reservas/${id}`)
      .then(res => {
        setReserva(res.data);
        setCorreosClientes(Array.from({ length: res.data.cantidadPersonas }, () => ""));
        setEmailErrors(Array.from({ length: res.data.cantidadPersonas }, () => ""));
      });

    // 2. Obtener tarifa
    axios.get(`http://localhost:8080/api/tarifas/${id}`)
      .then(res => setTarifa(res.data));

    // 3. Obtener descuento
    axios.get(`http://localhost:8080/api/descuentos/obtener/${id}`)
      .then(res => setDescuento(res.data));
  }, [id]);

  // Calcular totales
  const precioSinDescuento = tarifa?.precio || 0;
  const descuentoTotal = descuento?.descuentoTotal || 0;
  const precioConDescuento = precioSinDescuento - descuentoTotal;
  const precioConIva = +(precioConDescuento).toFixed(2); // YA INCLUYE IVA
  const precioSinIva = +(precioConDescuento / 1.19).toFixed(2);

  // Manejar cambios de correo
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

  // Generar PDF
  const generarPDFBlob = () => {
    const doc = new jsPDF();
    doc.setFont("helvetica", "bold");
    doc.setFontSize(16);
    doc.text("Comprobante de Pago", 70, 20);
    doc.setFont("helvetica", "normal");
    doc.setFontSize(12);
    doc.rect(10, 30, 190, 110);
    doc.text(`ID Reserva: ${reserva.idReserva}`, 20, 40);
    doc.text(`Fecha de Emisión: ${new Date().toISOString().split("T")[0]}`, 20, 50);
    doc.text(`Fecha de Reserva: ${reserva.fechaReserva.split("T")[0]}`, 20, 60);
    doc.text(`Hora de Inicio: ${reserva.horaInicio.split("T")[1]?.substring(0,5) || ""}`, 20, 70);
    doc.text(`Cantidad de Personas: ${reserva.cantidadPersonas}`, 20, 80);
    doc.text(`Número de Vueltas: ${reserva.numeroVueltas}`, 20, 90);
    doc.text(`Duración Total: ${tarifa?.duracionTotal || reserva.duracionTotal} min`, 20, 100);
    doc.text(`Cliente Responsable: ${reserva.nombreCliente}`, 20, 110);
    doc.text(`Precio sin descuento: $${precioSinDescuento.toFixed(2)}`, 20, 120);
    doc.text(`Descuento total: -$${descuentoTotal.toFixed(2)}`, 20, 130);
    doc.text(`Precio final (sin IVA): $${precioSinIva.toFixed(2)}`, 20, 140);
    doc.text(`Precio final (con IVA): $${precioConIva.toFixed(2)}`, 20, 150);

    // Correos de participantes
    let posY = 160;
    doc.rect(10, posY, 190, 40);
    doc.text("Correos de Participantes:", 20, posY + 10);
    correosClientes.forEach((correo, index) => {
      doc.text(`${index + 1}. ${correo}`, 20, posY + 20 + index * 10);
    });

    return doc.output("blob");
  };

  // Enviar comprobante
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Construir los parámetros
    const params = new URLSearchParams({
      idReserva: reserva.idReserva,
      fechaEmision: new Date().toISOString().split("T")[0],
      totalConIva: precioConIva.toString(),
      fechaReserva: reserva.fechaReserva.split("T")[0],
      horaInicio: reserva.horaInicio.split("T")[1]?.substring(0,5) || "",
      cantidadPersonas: reserva.cantidadPersonas,
      numeroVueltas: reserva.numeroVueltas,
      duracionTotal: tarifa?.duracionTotal || reserva.duracionTotal,
      nombreCliente: reserva.nombreCliente,
      precioFinalSinIVA: precioSinIva.toString(),
      correosClientes: correosClientes.join(",") // <--- aquí el cambio
    });

    const url = `http://localhost:8080/app/comprobantes?${params.toString()}`;
    console.log("URL comprobante:", url);

    try {
      const response = await fetch(url, { method: "POST" });
      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData);
      }
      alert("Pago realizado con éxito. Se ha generado y enviado el comprobante.");
      navigate("/comprobantes");
    } catch (error) {
      console.error("Error al enviar comprobante:", error);
      alert("Error al enviar comprobante. Revisa la consola.");
    }
  };

  if (!reserva || !tarifa || !descuento) {
    return <div>Cargando datos de la reserva...</div>;
  }

  return (
    <div className="container mt-4">
      <h2>Crear Comprobante de Pago</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label>ID Reserva</label>
          <input type="text" className="form-control" value={reserva.idReserva} readOnly />
        </div>
        <div className="mb-3">
          <label>Fecha de Emisión</label>
          <input type="date" className="form-control" value={new Date().toISOString().split("T")[0]} readOnly />
        </div>
        <div className="mb-3">
          <label>Fecha de Reserva</label>
          <input type="date" className="form-control" value={reserva.fechaReserva.split("T")[0]} readOnly />
        </div>
        <div className="mb-3">
          <label>Hora de Inicio</label>
          <input type="text" className="form-control" value={reserva.horaInicio.split("T")[1]?.substring(0,5) || ""} readOnly />
        </div>
        <div className="mb-3">
          <label>Cantidad de Personas</label>
          <input type="number" className="form-control" value={reserva.cantidadPersonas} readOnly />
        </div>
        <div className="mb-3">
          <label>Número de Vueltas</label>
          <input type="text" className="form-control" value={reserva.numeroVueltas} readOnly />
        </div>
        <div className="mb-3">
          <label>Duración Total (min)</label>
          <input type="number" className="form-control" value={tarifa?.duracionTotal || reserva.duracionTotal} readOnly />
        </div>
        <div className="mb-3">
          <label>Cliente Responsable</label>
          <input type="text" className="form-control" value={reserva.nombreCliente} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio sin descuento</label>
          <input type="number" className="form-control" value={precioSinDescuento.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Descuento total</label>
          <input type="number" className="form-control" value={descuentoTotal.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio final (sin IVA)</label>
          <input type="number" className="form-control" value={precioSinIva.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio final (con IVA)</label>
          <input type="number" className="form-control" value={precioConIva.toFixed(2)} readOnly />
        </div>

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

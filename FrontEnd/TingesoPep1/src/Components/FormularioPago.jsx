import React, { useEffect, useState } from "react";
import { obtenerReservaPorId, actualizarReserva } from "../Services/ReservaService";
import { useParams, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { jsPDF } from "jspdf";

const FormularioPago = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [reserva, setReserva] = useState(null);
    const [precioFinal, setPrecioFinal] = useState(0);
    
    useEffect(() => {
        obtenerReservaPorId(id).then(response => {
            const data = response.data;
            setReserva(data);
            calcularPrecioFinal(data);
        }).catch(error => {
            console.error("Error al obtener la reserva:", error);
        });
    }, [id]);


    const [correosClientes, setCorreosClientes] = useState([]);
    // Manejar cambios en los correos
    const handleCorreoChange = (index, value) => {
        const nuevosCorreos = [...correosClientes];
        nuevosCorreos[index] = value;
        setCorreosClientes(nuevosCorreos);
    };

    const generarPDF = () => {
        const doc = new jsPDF();
        doc.setFont("helvetica", "bold");
        doc.text("Detalle de Pago Individual", 20, 20);
    
        doc.setFont("helvetica", "normal");
        doc.text(`Fecha de Reserva: ${reserva.fechaReserva}`, 20, 40);
        doc.text(`Cantidad de Personas: ${reserva.cantidadPersonas}`, 20, 50);
        doc.text(`Número de Vueltas: ${reserva.numeroVueltas}`, 20, 60);
        doc.text(`Duración Total: ${reserva.duracionTotal} minutos`, 20, 70);
        
        const montoPorPersona = (precioFinal / reserva.cantidadPersonas).toFixed(2);
        doc.text(`Monto por Persona: $${montoPorPersona}`, 20, 80);
    
        doc.save("Detalle_Pago.pdf");
    };

    const generarExcel = () => {
        const data = [
            ["Concepto", "Monto"],
            ["Fecha Reserva", reserva.fechaReserva],
            ["Cantidad de Personas", reserva.cantidadPersonas],
            ["Número de Vueltas", reserva.numeroVueltas],
            ["Duración Total", reserva.duracionTotal + " minutos"],
            ["Precio Base", "$" + reserva.precioTotal.toFixed(2)],
            ["Descuento por Número de Personas", "$" + descuentoPorPersonas.toFixed(2)],
            ["Descuento por Cliente Frecuente", "$" + descuentoFrecuencia.toFixed(2)],
            ["Descuento por Cumpleaños", "$" + descuentoCumpleaños.toFixed(2)],
            ["Total a Pagar", "$" + precioFinal.toFixed(2)]
        ];
    
        const workbook = XLSX.utils.book_new();
        const worksheet = XLSX.utils.aoa_to_sheet(data);
        XLSX.utils.book_append_sheet(workbook, worksheet, "Resumen de Pago");
    
        XLSX.writeFile(workbook, "Resumen_Pago.xlsx");
    };

    const calcularPrecioFinal = (reserva) => {
        let descuentoPersonas = 0;
        let descuentoFrecuencia = 0;
        let descuentoDiaEspecial = 0;
        
        // 🔹 Descuentos por número de personas
        if (reserva.cantidadPersonas >= 3 && reserva.cantidadPersonas <= 5) descuentoPersonas = 0.10;
        else if (reserva.cantidadPersonas >= 6 && reserva.cantidadPersonas <= 10) descuentoPersonas = 0.20;
        else if (reserva.cantidadPersonas >= 11 && reserva.cantidadPersonas <= 15) descuentoPersonas = 0.30;
        
        // 🔹 Descuentos por frecuencia mensual del cliente
        const visitas = reserva.clienteResponsable?.frecuenciaMensual || 0;
        if (visitas >= 7) descuentoFrecuencia = 0.30;
        else if (visitas >= 5) descuentoFrecuencia = 0.20;
        else if (visitas >= 2) descuentoFrecuencia = 0.10;
    
        // 🔹 Descuento por cumpleaños
        const hoy = new Date().toISOString().split("T")[0]; // Fecha actual YYYY-MM-DD
        if (reserva.diaEspecial && reserva.clienteResponsable.fechaNacimiento === hoy) {
            if (reserva.cantidadPersonas >= 3 && reserva.cantidadPersonas <= 5) descuentoDiaEspecial = 0.50; // 1 cumpleañero
            else if (reserva.cantidadPersonas >= 6 && reserva.cantidadPersonas <= 10) descuentoDiaEspecial = 0.50 * 2; // 2 cumpleañeros
        }
    
        // 🔹 Calcular total de descuento
        const precioBase = reserva.precioTotal;
        const descuentoTotal = descuentoPersonas + descuentoFrecuencia + descuentoDiaEspecial;
        const precioFinalCalculado = precioBase * (1 - descuentoTotal);
    
        // Guardar descuentos individualmente para mostrarlos en pantalla
        setDescuentoPorPersonas(precioBase * descuentoPersonas);
        setDescuentoFrecuencia(precioBase * descuentoFrecuencia);
        setDescuentoCumpleaños(precioBase * descuentoDiaEspecial);
        setPrecioFinal(precioFinalCalculado);
    };
    

    const handlePago = () => {
        alert("Pago realizado con éxito. Redirigiendo...");
        navigate("/reservas");
    };

    return (
        <div className="container mt-4">
            <h4>Ingrese los correos de los participantes:</h4>
            {Array.from({ length: reserva.cantidadPersonas }, (_, index) => (
                <div className="mb-3" key={index}>
                    <label className="form-label">Correo del Cliente {index + 1}</label>
                    <input 
                        type="email" 
                        className="form-control"
                        alue={correosClientes[index] || ""}
                        onChange={(e) => handleCorreoChange(index, e.target.value)}
                        required
                    />
                </div>
            ))}
            <h2>Proceder al Pago</h2>
            {reserva && (
                <div>
                    <p><strong>Fecha de Reserva:</strong> {reserva.fechaReserva}</p>
                    <p><strong>Hora de Inicio:</strong> {reserva.horaInicio}</p>
                    <p><strong>Cantidad de Personas:</strong> {reserva.cantidadPersonas}</p>
                    <p><strong>Cliente Responsable:</strong> {reserva.clienteResponsable.nombre}</p>
                    <p><strong>Precio Base:</strong> ${reserva.precioTotal}</p>
                    <h4>Descuentos Aplicados:</h4>
                    <p>🔹 Descuento por número de personas: ${descuentoPorPersonas.toFixed(2)}</p>
                    <p>🔹 Descuento por cliente frecuente: ${descuentoFrecuencia.toFixed(2)}</p>
                    <p>🔹 Descuento por cumpleaños: ${descuentoCumpleaños.toFixed(2)}</p>
                    <p><strong>Precio Final con Descuentos:</strong> ${precioFinal.toFixed(2)}</p>
                    <button className="btn btn-success" onClick={handlePago}>Realizar Pago</button>
                    <button className="btn btn-primary mt-3" onClick={generarExcel}>Descargar Resumen en Excel</button>
                    <button className="btn btn-secondary mt-3" onClick={generarPDF}>Descargar Detalle en PDF</button>
                </div>
            )}
            
        </div>
        
    );
};

export default FormularioPago;

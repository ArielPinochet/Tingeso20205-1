import React, { useState } from "react";
import { jsPDF } from "jspdf";
import { crearComprobante } from "../Services/ComprobanteService";
import { useNavigate } from "react-router-dom";

const FormularioComprobante = () => {
    const [comprobante, setComprobante] = useState({ idReserva: "", fechaEmision: "", totalConIva: "", archivoPdf: "" });
    const navigate = useNavigate();

    const handleChange = (e) => {
        setComprobante({ ...comprobante, [e.target.name]: e.target.value });
    };

    const generarPDF = () => {
        const doc = new jsPDF();
    
        doc.setFont("helvetica", "bold");
        doc.setFontSize(16);
        doc.text("Comprobante de Pago", 70, 20);
    
        doc.setFont("helvetica", "normal");
        doc.setFontSize(12);
    
        // 🔹 Dibujar un recuadro para los datos
        doc.rect(10, 30, 190, 60); // x, y, width, height
    
        // 🔹 Insertar los datos dentro del recuadro
        doc.text(`ID Reserva: ${comprobante.idReserva}`, 20, 45);
        doc.text(`Fecha Emisión: ${comprobante.fechaEmision}`, 20, 55);
        doc.text(`Total con IVA: $${comprobante.totalConIva}`, 20, 65);
    
        // 🔹 Dibujar otro recuadro para detalles adicionales
        doc.rect(10, 100, 190, 60);
        doc.text("Detalles:", 20, 115);
        doc.text(comprobante.texto, 20, 125, { maxWidth: 170 });
    
        doc.save(`comprobante_${comprobante.idReserva}.pdf`);
    };
    

    const handleSubmit = async (e) => {
        e.preventDefault();
        const pdfBlob = generarPDF();

        const formData = new FormData();
        formData.append("idReserva", comprobante.idReserva);
        formData.append("fechaEmision", comprobante.fechaEmision);
        formData.append("totalConIva", comprobante.totalConIva);
        formData.append("archivoPdf", pdfBlob);

        await crearComprobante(formData);
        navigate("/comprobantes");
    };

    return (
        <div className="container mt-4">
            <h2>Agregar Comprobante</h2>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label">ID Reserva</label>
                    <input type="number" className="form-control" name="idReserva" value={comprobante.idReserva} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label className="form-label">Fecha de Emisión</label>
                    <input type="date" className="form-control" name="fechaEmision" value={comprobante.fechaEmision} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label className="form-label">Total con IVA</label>
                    <input type="number" className="form-control" name="totalConIva" value={comprobante.totalConIva} onChange={handleChange} required />
                </div>
                <button type="submit" className="btn btn-success">Generar y Guardar PDF</button>
                <button type="button" className="btn btn-secondary ms-2" onClick={() => navigate("/comprobantes")}>Cancelar</button>
            </form>
        </div>
    );
};

export default FormularioComprobante;

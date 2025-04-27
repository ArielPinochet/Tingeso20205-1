import React, { useState, useEffect } from "react";
import { jsPDF } from "jspdf";
import { obtenerReservas } from "../Services/ReservaService"; // 🔹 Asegurar que tengas este servicio para obtener reservas

const FormularioPDF = () => {
    const [comprobante, setComprobante] = useState({ idReserva: "", texto: "" });
    const [reservas, setReservas] = useState([]);
    
    useEffect(() => {
        obtenerReservas().then(response => setReservas(response.data));
    }, []);

    // 🔹 Generar un ID único si el usuario no selecciona uno
    const generarIdReserva = () => {
        return `R${Math.floor(1000 + Math.random() * 9000)}`; // Ejemplo: R1234
    };

    const handleChange = (e) => {
        setComprobante({ ...comprobante, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!comprobante.idReserva) {
            setComprobante({ ...comprobante, idReserva: generarIdReserva() });
        }
        generarPDF();
    };

    const generarPDF = () => {
        const doc = new jsPDF();

        doc.setFont("helvetica", "bold");
        doc.text("Comprobante de Pago", 20, 20);
        
        doc.setFont("helvetica", "normal");
        doc.text(`ID Reserva: ${comprobante.idReserva}`, 20, 40);
        doc.text(`Detalles:`, 20, 60);
        doc.text(comprobante.texto, 20, 70, { maxWidth: 170 });

        doc.save(`comprobante_${comprobante.idReserva}.pdf`);
    };

    return (
        <div className="container mt-4">
            <h2>Generar Comprobante de Pago</h2>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label">ID Reserva</label>
                    <select className="form-select" name="idReserva" value={comprobante.idReserva} onChange={handleChange}>
                        <option value="">Generar ID automáticamente</option>
                        {reservas.map(reserva => (
                            <option key={reserva.idReserva} value={reserva.idReserva}>{reserva.idReserva}</option>
                        ))}
                    </select>
                </div>
                <div className="mb-3">
                    <label className="form-label">Texto del Comprobante</label>
                    <textarea className="form-control" name="texto" value={comprobante.texto} onChange={handleChange} rows="4" required />
                </div>
                <button type="submit" className="btn btn-primary">Generar PDF</button>
            </form>
        </div>
    );
};

export default FormularioPDF;

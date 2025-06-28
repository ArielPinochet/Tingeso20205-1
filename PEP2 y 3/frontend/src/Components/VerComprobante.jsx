import React, { useEffect, useState } from "react";
import { obtenerComprobantePorId } from "../Services/ComprobanteService";
import { useParams, useNavigate } from "react-router-dom";

const VerComprobante = () => {
    const { id } = useParams(); // 🔹 Asegurarse de que es un número válido
    const [comprobante, setComprobante] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        console.log("ID obtenido desde URL:", id); // 🔹 Verificar qué se está pasando
        if (!isNaN(id)) { 
            obtenerComprobantePorId(id).then(response => setComprobante(response.data));
        } else {
            console.error("Error: ID inválido recibido", id);
        }
    }, [id]);

    if (!comprobante) {
        return <p>Cargando...</p>;
    }

    return (
        <div className="container mt-4">
            <h2>Detalles del Comprobante</h2>
            <p><strong>ID:</strong> {comprobante.idComprobante}</p>
            <p><strong>Fecha de Emisión:</strong> {comprobante.fechaEmision}</p>
            <p><strong>Total con IVA:</strong> ${comprobante.totalConIva}</p>


            {comprobante.archivoPdf && (
                <iframe
                  src={`data:application/pdf;base64,${comprobante.archivoPdf}`}
                  width="100%"
                  height="600px"
                  title="Comprobante PDF"
                />
            )}

            <button className="btn btn-secondary mt-3" onClick={() => navigate("/comprobantes")}>Volver</button>
        </div>
    );
};

export default VerComprobante;

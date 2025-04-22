import React, { useEffect, useState } from "react";
import { obtenerComprobantes, eliminarComprobante } from "../Services/ComprobanteService";
import { Link } from "react-router-dom";

const ListaComprobantes = () => {
    const [comprobantes, setComprobantes] = useState([]);

    useEffect(() => {
        obtenerComprobantes().then(response => {
            console.log("Comprobantes cargados:", response.data);
            setComprobantes(response.data);
        }).catch(error => console.error("Error cargando comprobantes:", error));
    }, []);

    const handleEliminar = (id) => {
        eliminarComprobante(id).then(() => {
            setComprobantes(comprobantes.filter(comprobante => comprobante.idComprobante !== id));
        });
    };

    return (
        <div className="container mt-4">
            <h2>Lista de Comprobantes de Pago</h2>
            <div className="row">
                {comprobantes.map(comprobante => (
                    <div key={comprobante.idComprobante} className="col-md-4 mb-3">
                        <div className="card shadow-sm">
                            <div className="card-body">
                                <h5 className="card-title">Comprobante #{comprobante.idComprobante}</h5>
                                <p className="card-text"><strong>Fecha:</strong> {comprobante.fechaEmision}</p>
                                <p className="card-text"><strong>Total con IVA:</strong> ${comprobante.totalConIva}</p>
                                
                                {/* 🔹 Botón para ver comprobante */}
                                <Link to={`/ver-comprobante/${comprobante.idComprobante}`} className="btn btn-info">Ver</Link>
                                
                                {/* 🔹 Botón para editar comprobante */}
                                <Link to={`/editar-comprobante/${comprobante.idComprobante}`} className="btn btn-warning ms-2">Editar</Link>
                                
                                {/* 🔹 Botón para eliminar comprobante */}
                                <button className="btn btn-danger ms-2" onClick={() => handleEliminar(comprobante.idComprobante)}>Eliminar</button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ListaComprobantes;

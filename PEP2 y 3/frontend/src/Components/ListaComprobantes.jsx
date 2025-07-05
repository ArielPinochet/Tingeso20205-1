import React, { useEffect, useState } from "react";
import { obtenerComprobantes } from "../Services/ComprobanteService";
import { Link } from "react-router-dom";

const ListaComprobantes = () => {
    const [comprobantes, setComprobantes] = useState([]);
    const [errorServicio, setErrorServicio] = useState(false);

    useEffect(() => {
        obtenerComprobantes()
            .then(response => {
                setComprobantes(response.data);
                setErrorServicio(false);
            })
            .catch(() => {
                setErrorServicio(true);
            });
    }, []);

    return (
        <div className="container mt-4">
            <h2>Lista de Comprobantes de Pago</h2>
            {errorServicio ? (
                <div className="alert alert-danger text-center" style={{ fontSize: "1.1rem" }}>
                    Servicio temporalmente fuera de servicio, inténtelo más tarde.
                </div>
            ) : (
                <div className="row">
                    {comprobantes.map(comprobante => (
                        <div key={comprobante.idComprobante} className="col-md-4 mb-3">
                            <div className="card shadow-sm">
                                <div className="card-body">
                                    <h5 className="card-title">Comprobante #{comprobante.idComprobante}</h5>
                                    <p className="card-text"><strong>Fecha:</strong> {comprobante.fechaEmision}</p>
                                    <p className="card-text"><strong>Total con IVA:</strong> ${comprobante.totalConIva}</p>
                                    <Link to={`/ver-comprobante/${comprobante.idComprobante}`} className="btn btn-info">Ver</Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ListaComprobantes;

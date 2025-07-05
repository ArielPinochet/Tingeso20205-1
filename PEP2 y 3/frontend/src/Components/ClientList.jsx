import React, { useEffect, useState } from "react";
import { obtenerClientes, eliminarCliente } from "../Services/ClientService";
import { Link } from "react-router-dom";

const ListaClientes = () => {
    const [clientes, setClientes] = useState([]);
    const [error, setError] = useState(false);

    useEffect(() => {
        obtenerClientes()
            .then(response => {
                setClientes(response.data);
                setError(false);
            })
            .catch(() => {
                setError(true);
            });
    }, []);

    const handleEliminar = (id) => {
        eliminarCliente(id).then(() => {
            setClientes(clientes.filter(cliente => cliente.idCliente !== id));
        });
    };

    return (
        <div className="container mt-4">
            <h2>Lista de Clientes</h2>
            {error && (
                <div className="alert alert-danger text-center" style={{ fontSize: "1.1rem" }}>
                    Servicio temporalmente fuera de servicio, inténtelo más tarde.
                </div>
            )}
            <Link
                to="/crear-cliente"
                className="btn btn-primary mb-3"
                style={{ pointerEvents: error ? "none" : "auto", opacity: error ? 0.5 : 1 }}
                tabIndex={error ? -1 : 0}
                aria-disabled={error ? "true" : "false"}
            >
                Agregar Cliente
            </Link>
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {clientes
                        .slice() // Copia para no mutar el estado original
                        .sort((a, b) => a.idCliente - b.idCliente)
                        .map(cliente => (
                            <tr key={cliente.idCliente}>
                                <td>{cliente.idCliente}</td>
                                <td>{cliente.nombre}</td>
                                <td>{cliente.email}</td>
                                <td>
                                    <Link to={`/editar-cliente/${cliente.idCliente}`} className="btn btn-warning">Editar</Link>
                                    <button className="btn btn-danger ms-2" onClick={() => handleEliminar(cliente.idCliente)}>Eliminar</button>
                                </td>
                            </tr>
                        ))}
                </tbody>
            </table>
        </div>
    );
};

export default ListaClientes;

import React, { useEffect, useState } from "react";
import { obtenerCarros, eliminarCarro } from "../Services/CarroService";
import { Link } from "react-router-dom";

const ListaCarros = () => {
    const [carros, setCarros] = useState([]);
    const [filtroEstado, setFiltroEstado] = useState(""); // 🔹 Estado de filtro

    useEffect(() => {
        obtenerCarros().then(response => setCarros(response.data));
    }, []);

    const handleEliminar = (id) => {
        eliminarCarro(id).then(() => {
            setCarros(carros.filter(carro => carro.codigoCarros !== id));
        });
    };

    // 🔹 Filtrar carros según el estado seleccionado
    const carrosFiltrados = filtroEstado ? carros.filter(carro => carro.estado === filtroEstado) : carros;

    return (
        <div className="container mt-4">
            <h2>Lista de Carros</h2>

            {/* 🔹 Botones para filtrar por estado */}
            <div className="mb-3">
                <button className="btn btn-info me-2" onClick={() => setFiltroEstado("")}>Mostrar Todos</button>
                <button className="btn btn-success me-2" onClick={() => setFiltroEstado("activo")}>Mostrar Activos</button>
                <button className="btn btn-warning" onClick={() => setFiltroEstado("mantenimiento")}>Mostrar Mantenimiento</button>
            </div>

            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Modelo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {carrosFiltrados.map(carro => (
                        <tr key={carro.codigoCarros}>
                            <td>{carro.codigoCarros}</td>
                            <td>{carro.modelo}</td>
                            <td>{carro.estado}</td>
                            <td>
                                <Link to={`/editar-carro/${carro.codigoCarros}`} className="btn btn-warning">Editar</Link>
                                <button className="btn btn-danger ms-2" onClick={() => handleEliminar(carro.codigoCarros)}>Eliminar</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ListaCarros;

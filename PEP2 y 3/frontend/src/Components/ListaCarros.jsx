import React, { useEffect, useState } from "react";
import { obtenerCarros, eliminarCarro } from "../Services/CarroService";
import { Link } from "react-router-dom";

const ListaCarros = () => {
    const [carros, setCarros] = useState([]);
    const [filtroEstado, setFiltroEstado] = useState("");
    const [error, setError] = useState(false);

    useEffect(() => {
        obtenerCarros()
            .then(response => {
                setCarros(response.data);
                setError(false);
            })
            .catch(() => {
                setError(true);
            });
    }, []);

    const handleEliminar = async (codigo) => {
        try {
            await eliminarCarro(codigo);
            setCarros(prevCarros => prevCarros.filter(carro => carro.codigoCarros !== codigo));
        } catch (error) {
            // Manejo de error opcional
        }
    };

    const carrosFiltrados = filtroEstado ? carros.filter(carro => carro.estado === filtroEstado) : carros;
    const carrosOrdenados = [...carrosFiltrados].sort((a, b) => a.codigoCarros - b.codigoCarros);

    return (
        <div className="container mt-4">
            <h2>Lista de Carros</h2>
            {error && (
                <div className="alert alert-danger text-center" style={{ fontSize: "1.1rem" }}>
                    Servicio temporalmente fuera de servicio, inténtelo más tarde.
                </div>
            )}
            <div className="mb-3 d-flex justify-content-between">
                <div>
                    {/* Aquí puedes agregar botones de filtro si lo deseas */}
                </div>
            </div>
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Modelo</th>
                    </tr>
                </thead>
                <tbody>
                    {carrosOrdenados.map(carro => (
                        <tr key={carro.codigoCarros}>
                            <td>{carro.codigoCarros}</td>
                            <td>{carro.modelo}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ListaCarros;

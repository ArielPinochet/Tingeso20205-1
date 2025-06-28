import React, { useEffect, useState } from "react";
import { obtenerCarros, eliminarCarro } from "../Services/CarroService";
import { Link } from "react-router-dom";

const ListaCarros = () => {
    const [carros, setCarros] = useState([]);
    const [filtroEstado, setFiltroEstado] = useState(""); // 🔹 Estado de filtro

    useEffect(() => {
        obtenerCarros().then(response => setCarros(response.data));
    }, []);

   const handleEliminar = async (codigo) => {
    try {
        console.log(`🔹 Eliminando carro con código: ${codigo}`);
        
        await eliminarCarro(codigo); // 🔹 Esperar a que la eliminación se complete
        setCarros(prevCarros => prevCarros.filter(carro => carro.codigoCarros !== codigo));
        
        console.log("✔️ Carro eliminado con éxito");
    } catch (error) {
        console.error("🚨 Error al eliminar carro:", error);
        
        if (error.response) {
            console.error("🔹 Detalles del error:", error.response.data);
            console.error("🔹 Código de estado:", error.response.status);
        } else {
            console.error("🔹 No hay respuesta del servidor.");
        }
    }
};


    // 🔹 Filtrar carros según el estado seleccionado
    const carrosFiltrados = filtroEstado ? carros.filter(carro => carro.estado === filtroEstado) : carros;

    // 🔹 Ordenar por código de carro de menor a mayor
    const carrosOrdenados = [...carrosFiltrados].sort((a, b) => a.codigoCarros - b.codigoCarros);

    return (
        <div className="container mt-4">
            <h2>Lista de Carros</h2>

            {/* 🔹 Botón para agregar un nuevo carro */}
            <div className="mb-3 d-flex justify-content-between">

                {/* 🔹 Botones para filtrar por estado */}
                <div>
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

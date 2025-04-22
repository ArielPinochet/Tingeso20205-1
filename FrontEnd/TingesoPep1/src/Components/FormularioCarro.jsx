import React, { useEffect, useState } from "react";
import { crearCarro, obtenerCarros, obtenerCarroPorId, actualizarCarro } from "../Services/CarroService";
import { useNavigate, useParams } from "react-router-dom";

const FormularioCarro = () => {
    const [carro, setCarro] = useState({ codigoCarros: "", modelo: "SodiKart RT8", estado: "" });
    const [carrosExistentes, setCarrosExistentes] = useState([]);
    const navigate = useNavigate();
    const { id } = useParams();

    // 🔹 Cargar carros existentes para filtrar los disponibles
    useEffect(() => {
        obtenerCarros().then(response => {
            setCarrosExistentes(response.data.map(carro => carro.codigoCarros));
        });
    }, []);

    // 🔹 Si estamos editando un carro, cargar sus datos
    useEffect(() => {
        if (id) {
            obtenerCarroPorId(id).then(response => {
                const carroCargado = response.data;
                setCarro({
                    codigoCarros: carroCargado.codigoCarros,
                    modelo: "SodiKart RT8", // 🔹 Se mantiene fijo
                    estado: carroCargado.estado
                });
            });
        }
    }, [id]);

    // 🔹 Generar lista de códigos disponibles (filtrando los existentes)
    const opcionesDisponibles = Array.from({ length: 15 }, (_, i) => `K${String(i + 1).padStart(3, '0')}`)
        .filter(codigo => !carrosExistentes.includes(codigo));

    const handleChange = (e) => {
        setCarro({ ...carro, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (id) {
            actualizarCarro(id, carro).then(() => navigate("/Carros"));
        } else {
            crearCarro(carro).then(() => navigate("/Carros"));
        }
    };

    return (
        <div className="container mt-4">
            <h2>{id ? "Editar Carro" : "Agregar Carro"}</h2>
            <form onSubmit={handleSubmit}>
                {/* 🔹 Código solo seleccionable si el carro no existe */}
                {!id && (
                    <div className="mb-3">
                        <label className="form-label">Código del Carro</label>
                        <select className="form-select" name="codigoCarros" value={carro.codigoCarros} onChange={handleChange} required>
                            <option value="">Selecciona un código</option>
                            {opcionesDisponibles.map(codigo => (
                                <option key={codigo} value={codigo}>{codigo}</option>
                            ))}
                        </select>
                    </div>
                )}
                {/* 🔹 Modelo siempre fijo */}
                <div className="mb-3">
                    <label className="form-label">Modelo</label>
                    <input type="text" className="form-control" name="modelo" value={carro.modelo} readOnly />
                </div>
                {/* 🔹 Estado modificable */}
                <div className="mb-3">
                    <label className="form-label">Estado</label>
                    <select className="form-select" name="estado" value={carro.estado} onChange={handleChange} required>
                        <option value="">Selecciona un estado</option>
                        <option value="activo">Activo</option>
                        <option value="mantenimiento">Mantenimiento</option>
                    </select>
                </div>
                <button type="submit" className="btn btn-success">{id ? "Actualizar" : "Guardar"}</button>
                <button type="button" className="btn btn-secondary ms-2" onClick={() => navigate("/carros")}>Cancelar</button>
            </form>
        </div>
    );
};

export default FormularioCarro;

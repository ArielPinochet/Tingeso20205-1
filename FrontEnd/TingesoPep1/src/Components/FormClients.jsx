import React, { useEffect, useState } from "react";
import { crearCliente, obtenerClientePorId, actualizarCliente } from "../services/ClientService";
import { useNavigate, useParams } from "react-router-dom";

const FormularioCliente = () => {
    const [cliente, setCliente] = useState({ nombre: "", email: "", fechaNacimiento: "" });
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        if (id) {
            obtenerClientePorId(id).then(response => setCliente(response.data));
        }
    }, [id]);

    const handleChange = (e) => {
        setCliente({ ...cliente, [e.target.name]: e.target.value });
    };
    
    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Cliente enviado:", cliente); // Depuración
    
        if (id) {
            actualizarCliente(id, cliente).then(() => navigate("/"));
        } else {
            crearCliente(cliente).then(() => navigate("/"));
        }
    };
    

    return (
        <div className="container mt-4">
            <h2>{id ? "Editar Cliente" : "Agregar Cliente"}</h2>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label>Nombre</label>
                    <input type="text" className="form-control" name="nombre" value={cliente.nombre} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label>Email</label>
                    <input type="email" className="form-control" name="email" value={cliente.email} onChange={handleChange} required />
                </div>
                <div className="mb-3">
                    <label>Fecha de Nacimiento</label>
                    <input type="date" className="form-control" name="fechaNacimiento" value={cliente.fechaNacimiento} onChange={handleChange} required />
                </div>
                <button type="submit" className="btn btn-success">{id ? "Actualizar" : "Guardar"}</button>
            </form>
        </div>
    );
};

export default FormularioCliente;

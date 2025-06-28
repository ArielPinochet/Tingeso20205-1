import React, { useEffect, useState, useRef } from "react";
import { crearCliente, obtenerClientePorId, editarCorreoCliente } from "../Services/ClientService";
import { useNavigate, useParams } from "react-router-dom";

const FormularioCliente = () => {
    const [cliente, setCliente] = useState({ nombre: "", email: "" });
    const [cargando, setCargando] = useState(false);
    const [guardado, setGuardado] = useState(false);
    const [error, setError] = useState(false);
    const navigate = useNavigate();
    const { id } = useParams();
    const timeoutRef = useRef(null);

    useEffect(() => {
        if (id) {
            setCargando(true);
            obtenerClientePorId(id).then(response => {
                setCliente({
                    nombre: response.data.nombre || "",
                    email: response.data.email || ""
                });
                setCargando(false);
            });
        }
    }, [id]);

    useEffect(() => {
        if (cargando) {
            timeoutRef.current = setTimeout(() => {
                setCargando(false);
                setError(true);
            }, 15000);
        } else {
            clearTimeout(timeoutRef.current);
        }
        return () => clearTimeout(timeoutRef.current);
    }, [cargando]);

    const handleChange = (e) => {
        setCliente({ ...cliente, [e.target.name]: e.target.value });
    };
    
    const handleSubmit = (e) => {
        e.preventDefault();
        setCargando(true);
        setError(false);
        if (id) {
            editarCorreoCliente(cliente.nombre, cliente.email)
                .then(() => {
                    setCargando(false);
                    setGuardado(true);
                })
                .catch(() => {
                    setCargando(false);
                    setError(true);
                });
        } else {
            crearCliente(cliente)
                .then(() => {
                    setCargando(false);
                    setGuardado(true);
                })
                .catch(() => {
                    setCargando(false);
                    setError(true);
                });
        }
    };

    return (
        <div className="container mt-4">
            <h2>{id ? "Editar Cliente" : "Agregar Cliente"}</h2>

            {/* Modal de cargando */}
            {cargando && (
                <div style={{
                    position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
                    background: "rgba(0,0,0,0.3)", zIndex: 3000,
                    display: "flex", alignItems: "center", justifyContent: "center"
                }}>
                    <div style={{
                        background: "#fff", padding: "2rem", borderRadius: "10px",
                        boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
                    }}>
                        <h5>Guardando cliente...</h5>
                        <div className="spinner-border text-primary mt-3" role="status">
                            <span className="visually-hidden">Guardando...</span>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal de error */}
            {error && (
                <div style={{
                    position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
                    background: "rgba(0,0,0,0.3)", zIndex: 3000,
                    display: "flex", alignItems: "center", justifyContent: "center"
                }}>
                    <div style={{
                        background: "#fff", padding: "2rem", borderRadius: "10px",
                        boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
                    }}>
                        <h5>Hubo un error al guardar. Intente más tarde.</h5>
                        <button className="btn btn-danger mt-3" onClick={() => setError(false)}>
                            OK
                        </button>
                    </div>
                </div>
            )}

            {/* Modal de guardado */}
            {guardado && (
                <div style={{
                    position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
                    background: "rgba(0,0,0,0.3)", zIndex: 3000,
                    display: "flex", alignItems: "center", justifyContent: "center"
                }}>
                    <div style={{
                        background: "#fff", padding: "2rem", borderRadius: "10px",
                        boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
                    }}>
                        <h5>Cliente guardado correctamente</h5>
                        <button className="btn btn-success mt-3" onClick={() => {
                            setGuardado(false);
                            navigate("/");
                        }}>
                            OK
                        </button>
                    </div>
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label>Nombre</label>
                    <input
                        type="text"
                        className="form-control"
                        name="nombre"
                        value={cliente.nombre}
                        onChange={handleChange}
                        required
                        disabled={!!id}
                    />
                </div>
                <div className="mb-3">
                    <label>Email</label>
                    <input
                        type="email"
                        className="form-control"
                        name="email"
                        value={cliente.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit" className="btn btn-success">{id ? "Actualizar" : "Guardar"}</button>
            </form>
        </div>
    );
};

export default FormularioCliente;

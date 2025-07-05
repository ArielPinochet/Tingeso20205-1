import React, { useEffect, useState, useRef } from "react";
import { crearCliente, obtenerClientePorId, editarCorreoCliente, obtenerClientes } from "../Services/ClientService";
import { useNavigate, useParams } from "react-router-dom";

const EMAIL_DOMAINS = [
    "@gmail.com",
    "@outlook.com",
    "@hotmail.com",
    "@yahoo.com",
    "@icloud.com",
    "@usach.cl",
    "Otro"
];

const FormularioCliente = () => {
    const [cliente, setCliente] = useState({ nombre: "", email: "" });
    const [cargando, setCargando] = useState(false);
    const [guardado, setGuardado] = useState(false);
    const [error, setError] = useState(false);
    const [nombreExistente, setNombreExistente] = useState(false);
    const [clientes, setClientes] = useState([]);
    const [capsLockOn, setCapsLockOn] = useState(false);
    const [showSpaceWarning, setShowSpaceWarning] = useState(false);
    const [emailUser, setEmailUser] = useState(""); // solo la parte antes de @
    const [emailDomain, setEmailDomain] = useState(EMAIL_DOMAINS[0]);
    const [customDomain, setCustomDomain] = useState(""); // para "Otro"
    const navigate = useNavigate();
    const { id } = useParams();
    const timeoutRef = useRef(null);

    useEffect(() => {
        obtenerClientes().then(res => setClientes(res.data));
    }, []);

    useEffect(() => {
        if (id) {
            setCargando(true);
            obtenerClientePorId(id).then(response => {
                setCliente({
                    nombre: response.data.nombre || "",
                    email: response.data.email || ""
                });
                // Si hay email, separa usuario y dominio
                if (response.data.email) {
                    const [user, domain] = response.data.email.split("@");
                    setEmailUser(user || "");
                    const domainWithAt = domain ? "@" + domain : EMAIL_DOMAINS[0];
                    if (EMAIL_DOMAINS.includes(domainWithAt)) {
                        setEmailDomain(domainWithAt);
                        setCustomDomain("");
                    } else {
                        setEmailDomain("Otro");
                        setCustomDomain(domain || "");
                    }
                }
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

    // Normaliza el nombre: quita espacios y pasa a minúsculas
    const normalizarNombre = (nombre) => nombre.replace(/\s+/g, '').toLowerCase();

    // Verifica si el nombre ya existe (excepto si está editando y el nombre es el mismo)
    useEffect(() => {
        if (!id && cliente.nombre) {
            const nombreInput = normalizarNombre(cliente.nombre);
            const existe = clientes.some(
                c => normalizarNombre(c.nombre) === nombreInput
            );
            setNombreExistente(existe);
        } else {
            setNombreExistente(false);
        }
    }, [cliente.nombre, clientes, id]);

    const handleChange = (e) => {
        if (e.target.name === "nombre") {
            setCliente({ ...cliente, nombre: e.target.value.replace(/\s+/g, '').toLowerCase() });
        }
    };

    // Email handlers
    const handleEmailUserChange = (e) => {
        setEmailUser(e.target.value.replace(/\s+/g, ''));
    };

    const handleEmailDomainChange = (e) => {
        setEmailDomain(e.target.value);
        if (e.target.value !== "Otro") {
            setCustomDomain("");
        }
    };

    const handleCustomDomainChange = (e) => {
        setCustomDomain(e.target.value.replace(/\s+/g, ''));
    };

    // Actualiza el email completo en cliente
    useEffect(() => {
        let email = emailUser;
        if (emailUser) {
            if (emailDomain === "Otro" && customDomain) {
                email += "@" + customDomain;
            } else if (emailDomain !== "Otro") {
                email += emailDomain;
            }
        }
        setCliente((prev) => ({ ...prev, email }));
    }, [emailUser, emailDomain, customDomain]);

    // Mostrar globo efímero si se presiona espacio
    const handleNombreKeyDown = (e) => {
        if (e.code === "Space" || e.key === " ") {
            setShowSpaceWarning(true);
            setTimeout(() => setShowSpaceWarning(false), 1200);
        }
        setCapsLockOn(e.getModifierState && e.getModifierState("CapsLock"));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (nombreExistente) return;
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
                            navigate("/clientes");
                        }}>
                            OK
                        </button>
                    </div>
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div className="mb-3" style={{ position: "relative" }}>
                    <label>Nombre</label>
                    <input
                        type="text"
                        className={`form-control ${cliente.nombre
                            ? (nombreExistente ? "is-invalid" : "is-valid")
                            : ""}`}
                        name="nombre"
                        value={cliente.nombre}
                        onChange={handleChange}
                        onKeyUp={e => setCapsLockOn(e.getModifierState && e.getModifierState("CapsLock"))}
                        onKeyDown={handleNombreKeyDown}
                        required
                        disabled={!!id}
                    />
                    {/* Globo efímero si se presiona espacio */}
                    {showSpaceWarning && (
                        <div
                            style={{
                                position: "absolute",
                                top: 38,
                                left: 0,
                                background: "#dc3545",
                                color: "#fff",
                                padding: "4px 12px",
                                borderRadius: "8px",
                                fontSize: "0.95rem",
                                zIndex: 10,
                                boxShadow: "0 2px 8px rgba(0,0,0,0.12)",
                                animation: "fadeInOut 1.2s"
                            }}
                        >
                            No se permite el uso de espacios en el nombre.
                        </div>
                    )}
                    {/* Aviso si Caps Lock está activado */}
                    {capsLockOn && (
                        <div className="text-danger mt-1" style={{ display: "block", fontWeight: "bold" }}>
                            ¡Atención! Caps Lock está activado. El nombre solo se guarda en minúsculas y sin espacios.
                        </div>
                    )}
                    {/* Aviso si el usuario escribe mayúsculas o espacios */}
                    {/[A-Z\s]/.test(cliente.nombre) && (
                        <div className="text-warning mt-1" style={{ display: "block" }}>
                            El nombre de usuario solo se guarda en minúsculas y sin espacios.
                        </div>
                    )}
                    {cliente.nombre && !nombreExistente && !/[A-Z\s]/.test(cliente.nombre) && (
                        <div className="valid-feedback" style={{ display: "block" }}>
                            ✔ Nombre disponible
                        </div>
                    )}
                    {nombreExistente && (
                        <div className="invalid-feedback" style={{ display: "block" }}>
                            El nombre de usuario ya existe. Elija otro.
                        </div>
                    )}
                </div>
                <div className="mb-3">
                    <label>Email</label>
                    <div className="input-group">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="usuario"
                            value={emailUser}
                            onChange={handleEmailUserChange}
                            required
                            disabled={!!id}
                        />
                        <select
                            className="form-select"
                            value={emailDomain}
                            onChange={handleEmailDomainChange}
                            disabled={!!id}
                        >
                            {EMAIL_DOMAINS.map((domain) => (
                                <option key={domain} value={domain}>{domain === "Otro" ? "Otro..." : domain}</option>
                            ))}
                        </select>
                        {emailDomain === "Otro" && (
                            <input
                                type="text"
                                className="form-control"
                                placeholder="dominio.com"
                                value={customDomain}
                                onChange={handleCustomDomainChange}
                                required
                                disabled={!!id}
                            />
                        )}
                    </div>
                </div>
                <button
                    type="submit"
                    className="btn btn-success"
                    disabled={nombreExistente || cargando}
                >
                    {id ? "Actualizar" : "Guardar"}
                </button>
                <button
                    type="button"
                    className="btn btn-secondary ms-2"
                    onClick={() => navigate("/clientes")}
                    disabled={cargando}
                >
                    Cancelar
                </button>
            </form>
            {/* Animación para el globo */}
            <style>
                {`
                @keyframes fadeInOut {
                    0% { opacity: 0; transform: translateY(-10px);}
                    10% { opacity: 1; transform: translateY(0);}
                    90% { opacity: 1; transform: translateY(0);}
                    100% { opacity: 0; transform: translateY(-10px);}
                }
                `}
            </style>
        </div>
    );
};

export default FormularioCliente;

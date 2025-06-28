import React, { useEffect, useState } from "react";
import { obtenerReservas, eliminarReserva } from "../Services/ReservaService";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";

const ADMIN_KEY = import.meta.env.VITE_ADMIN_KEY ; 

const ListaReservas = () => {
  const [reservas, setReservas] = useState([]);
  const [reservasConComprobante, setReservasConComprobante] = useState([]);
  const [modoAdmin, setModoAdmin] = useState(false);
  const [mostrarClaveAdmin, setMostrarClaveAdmin] = useState(false);
  const [claveInput, setClaveInput] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    obtenerReservas()
      .then((response) => {
        const data = response.data;
        const reservasArray = Array.isArray(data) ? data : [data];
        setReservas(reservasArray);
      })
      .catch((error) => {
        console.error("🚨 Error al obtener reservas:", error);
      });

    axios.get("http://localhost:8080/app/reservas/con-comprobante")
      .then((response) => {
        setReservasConComprobante(response.data);
      })
      .catch((error) => {
        console.error("🚨 Error al obtener reservas con comprobante:", error);
      });
  }, []);

  const handleAdminClick = () => {
    if (!modoAdmin) {
      setMostrarClaveAdmin(true);
      setClaveInput("");
    } else {
      setModoAdmin(false);
    }
  };

  const handleAceptarClave = () => {
    if (claveInput === ADMIN_KEY) {
      setModoAdmin(true);
      setMostrarClaveAdmin(false);
      setClaveInput("");
    } else {
      alert("Clave incorrecta.");
      setClaveInput("");
    }
  };

  const handleCerrarClave = () => {
    setMostrarClaveAdmin(false);
    setClaveInput("");
  };

  const handleEliminar = async (id) => {
    try {
      await eliminarReserva(id);
      setReservas((prevReservas) => prevReservas.filter((reserva) => reserva.idReserva !== id));
    } catch (error) {
      console.error("🚨 Error al eliminar reserva:", error);
    }
  };

  return (
    <>
      {mostrarClaveAdmin && (
        <div style={{
          position: "fixed",
          top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 2000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "10px",
            boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px"
          }}>
            <h5>Clave de Administrador</h5>
            <input
              type="password"
              className="form-control mb-3"
              placeholder="Ingrese clave"
              value={claveInput}
              onChange={e => setClaveInput(e.target.value)}
              onKeyDown={e => {
                if (e.key === "Enter") handleAceptarClave();
                if (e.key === "Escape") handleCerrarClave();
              }}
              autoFocus
            />
            <div className="d-flex justify-content-end">
              <button className="btn btn-secondary me-2" onClick={handleCerrarClave}>Cerrar</button>
              <button className="btn btn-primary" onClick={handleAceptarClave}>Aceptar</button>
            </div>
          </div>
        </div>
      )}
      <div className="container mt-4">
        <h2>Lista de Reservas</h2>
        <Link to="/crear-reserva" className="btn btn-primary mb-3">Nueva Reserva</Link>
        <button
          className={`btn btn-${modoAdmin ? "danger" : "secondary"} ms-2 mb-3`}
          onClick={handleAdminClick}
        >
          {modoAdmin ? "Salir de Administrador" : "ADMINISTRADOR"}
        </button>

        <div className="row">
          {reservas.length > 0 ? reservas.map((reserva) => {
            const hoy = new Date();
            const yyyy = hoy.getFullYear();
            const mm = String(hoy.getMonth() + 1).padStart(2, '0');
            const dd = String(hoy.getDate()).padStart(2, '0');
            const fechaHoy = `${yyyy}-${mm}-${dd}`;

            let fechaReservaSolo = reserva.fechaReserva;
            if (fechaReservaSolo && fechaReservaSolo.includes("T")) {
              fechaReservaSolo = fechaReservaSolo.split("T")[0];
            }

            const esHoy = fechaReservaSolo === fechaHoy;
            const fechaReservaDate = new Date(reserva.fechaReserva);
            const fechaYaPaso = fechaReservaDate < hoy;
            const tieneComprobante = reservasConComprobante.includes(reserva.idReserva);

            const accionesDeshabilitadas = (fechaYaPaso || tieneComprobante) && !modoAdmin;

            return (
              <div key={reserva.idReserva} className="col-md-4 mb-3">
                <div className="card shadow-sm">
                  <div className="card-body">
                    <h5 className="card-title">Reserva #{reserva.idReserva || "Desconocida"}</h5>
                    <p><strong>Fecha:</strong> {reserva.fechaReserva ? new Date(reserva.fechaReserva).toLocaleDateString() : "Sin definir"}</p>
                    <p><strong>Hora Inicio:</strong> {
                      reserva.horaInicio
                        ? (reserva.horaInicio.includes("T")
                            ? reserva.horaInicio.split("T")[1].substring(0,5)
                            : reserva.horaInicio.substring(0,5))
                    : "Sin definir"
                    }</p>
                    <p><strong>Número de Vueltas:</strong> {reserva.numeroVueltas || 0}</p>
                    <p><strong>Duración:</strong> {reserva.duracionTotal ? `${reserva.duracionTotal} minutos` : "No calculada"}</p>
                    <p><strong>Cantidad de Personas:</strong> {reserva.cantidadPersonas || "No especificada"}</p>
                    <p><strong>Día Especial:</strong> {reserva.diaEspecial === true || reserva.diaEspecial === "true" ? "Sí" : "No"}</p>
                    <p><strong>Cliente Responsable:</strong> {reserva.nombreCliente || "No especificado"}</p>

                    {tieneComprobante ? (
                      <div className="alert alert-secondary mt-2">Reserva Pagada</div>
                    ) : null}

                    {accionesDeshabilitadas ? (
                      <div className="alert alert-info mt-2">
                        {tieneComprobante
                          ? "Reserva pagada, no se puede modificar ni eliminar."
                          : "La fecha de la reserva ya pasó, no se puede modificar ni eliminar."}
                      </div>
                    ) : (
                      <>
                        <button
                          className="btn btn-warning"
                          onClick={() => navigate(`/editar-reserva/${reserva.idReserva}`)}
                          disabled={esHoy && !modoAdmin} // No permite editar si es hoy y no es admin
                          title={esHoy && !modoAdmin ? "No se puede editar una reserva el mismo día" : ""}
                        >
                          Editar
                        </button>
                        <button
                          className="btn btn-danger ms-2"
                          onClick={() => handleEliminar(reserva.idReserva)}
                          disabled={esHoy && !modoAdmin} // No permite eliminar si es hoy y no es admin
                          title={esHoy && !modoAdmin ? "No se puede eliminar una reserva el mismo día" : ""}
                        >
                          Eliminar
                        </button>
                        <button
                          className="btn btn-success ms-2"
                          onClick={() => navigate(`/crear-comprobante/${reserva.idReserva}`)}
                          disabled={false} // Siempre permite pagar, incluso si es hoy
                          title=""
                        >
                          Proceder al Pago
                        </button>
                      </>
                    )}
                    {/* Si es admin, muestra el botón eliminar aunque esté deshabilitado para usuarios normales */}
                    {modoAdmin && accionesDeshabilitadas && (
                      <button
                        className="btn btn-danger mt-2"
                        onClick={() => handleEliminar(reserva.idReserva)}
                      >
                        Eliminar (Admin)
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          }) : (
            <div className="col-12 text-center mt-4">
              <p className="alert alert-info">No hay reservas disponibles</p>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default ListaReservas;

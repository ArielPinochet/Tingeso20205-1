import React, { useEffect, useState } from "react";
import { obtenerReservas, eliminarReserva } from "../Services/ReservaService";
import { useNavigate, Link } from "react-router-dom";

const ListaReservas = () => {
  const [reservas, setReservas] = useState([]);
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
  }, []);

  const handleEliminar = async (id) => {
    try {
      console.log(`🔹 Eliminando reserva con ID: ${id}`);
      await eliminarReserva(id);
      setReservas((prevReservas) => prevReservas.filter((reserva) => reserva.idReserva !== id));
      console.log("✔️ Reserva eliminada con éxito");
    } catch (error) {
      console.error("🚨 Error al eliminar reserva:", error);
    }
  };

  return (
    <div className="container mt-4">
      <h2>Lista de Reservas</h2>
      <Link to="/crear-reserva" className="btn btn-primary mb-3">Nueva Reserva</Link>

      <div className="row">
        {reservas.length > 0 ? reservas.map((reserva) => (
          <div key={reserva.idReserva} className="col-md-4 mb-3">
            <div className="card shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Reserva #{reserva.idReserva || "Desconocida"}</h5>
                <p><strong>Fecha:</strong> {reserva.fechaReserva ? new Date(reserva.fechaReserva).toLocaleDateString() : "Sin definir"}</p>
                <p><strong>Hora Inicio:</strong> {reserva.horaInicio || "Sin definir"}</p>
                <p><strong>Número de Vueltas:</strong> {reserva.numeroVueltas || 0}</p>
                <p><strong>Duración:</strong> {reserva.duracionTotal ? `${reserva.duracionTotal} minutos` : "No calculada"}</p>
                <p><strong>Cantidad de Personas:</strong> {reserva.cantidadPersonas || "No especificada"}</p>
                <p><strong>Día Especial:</strong> {reserva.diaEspecial === true || reserva.diaEspecial === "true" ? "Sí" : "No"}</p>
                <p><strong>Cliente Responsable:</strong> {reserva.nombreCliente || "No especificado"}</p>

                {reserva.comprobantePago ? (
                  <div className="alert alert-secondary mt-2">Reserva Pagada</div>
                ) : (
                  <>
                    <button className="btn btn-warning" onClick={() => navigate(`/editar-reserva/${reserva.idReserva}`)}>Editar</button>
                    <button className="btn btn-danger ms-2" onClick={() => handleEliminar(reserva.idReserva)}>Eliminar</button>
                    <button className="btn btn-success ms-2" onClick={() => navigate(`/crear-comprobante/${reserva.idReserva}`)}>Proceder al Pago</button>
                  </>
                )}
              </div>
            </div>
          </div>
        )) : (
          <div className="col-12 text-center mt-4">
            <p className="alert alert-info">No hay reservas disponibles</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ListaReservas;

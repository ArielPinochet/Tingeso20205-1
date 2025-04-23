import React, { useEffect, useState } from "react";
import { obtenerReservas, eliminarReserva } from "../services/ReservaService";
import { useNavigate, Link } from "react-router-dom";

const ListaReservas = () => {
  const [reservas, setReservas] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    obtenerReservas()
      .then((response) => {
        const data = response.data;
        // Convertir data a arreglo si no lo es
        const reservasArray = Array.isArray(data) ? data : [data];
        setReservas(reservasArray);
      })
      .catch((error) => {
        console.error("Error al obtener reservas:", error);
      });
  }, []);

  const handleEliminar = (id) => {
    eliminarReserva(id).then(() => {
      setReservas(reservas.filter((reserva) => reserva.idReserva !== id));
    });
  };

  return (
    <div className="container mt-4">
      <h2>Lista de Reservas</h2>
      <Link to="/crear-reserva" className="btn btn-primary mb-3">
        Nueva Reserva
      </Link>

      <div className="row">
        {reservas.map((reserva) => (
          <div key={reserva.idReserva} className="col-md-4 mb-3">
            <div className="card shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Reserva #{reserva.idReserva}</h5>
                <p>
                  <strong>Fecha:</strong>{" "}
                  {new Date(reserva.fechaReserva).toLocaleDateString()}
                </p>
                <p>
                  <strong>Hora Inicio:</strong> {reserva.horaInicio}
                </p>
                <p>
                  <strong>Número de Vueltas:</strong> {reserva.numeroVueltas}
                </p>
                <p>
                  <strong>Duración:</strong> {reserva.duracionTotal} minutos
                </p>
                <p>
                  <strong>Cantidad de Personas:</strong> {reserva.cantidadPersonas}
                </p>
                <p>
                  <strong>Día Especial:</strong>{" "}
                  {reserva.diaEspecial === true || reserva.diaEspecial === "true"
                    ? "Sí"
                    : "No"}
                </p>
                <p>
                  <strong>Cliente Responsable:</strong>{" "}
                  {reserva.clienteResponsable?.nombre}
                </p>
                <p>
                  <strong>Precio Total:</strong> ${reserva.precioTotal}
                </p>
                <button
                  className="btn btn-warning"
                  onClick={() => navigate(`/editar-reserva/${reserva.idReserva}`)}
                >
                  Editar
                </button>
                <button
                  className="btn btn-danger ms-2"
                  onClick={() => handleEliminar(reserva.idReserva)}
                >
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ListaReservas;

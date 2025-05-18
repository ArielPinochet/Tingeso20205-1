// Archivo: ReservasCliente.jsx
import React, { useState, useEffect } from "react";
import { obtenerReservas } from "../Services/ReservaService";

const ReservasCliente = ({ clienteId }) => {
  const [reservasCliente, setReservasCliente] = useState([]);

  useEffect(() => {
    obtenerReservas()
      .then((response) => {
        if (Array.isArray(response.data)) {
          // Depuramos: mostramos cada reserva y el id del cliente de cada una
          response.data.forEach((reserva) => {
            const idResCliente = reserva.clienteResponsable?.idCliente;
            console.log(
              "Reserva recibida:",
              reserva.idReserva,
              " clienteResponsable.idCliente:",
              idResCliente
            );
          });
          // Filtramos: aseguramos convertir ambos valores a número
          const reservasFiltradas = response.data.filter((reserva) => {
            const idResCliente = reserva.clienteResponsable?.idCliente;
            console.log(
              "Comparando reserva cliente id:",
              idResCliente,
              "con prop clienteId:",
              clienteId
            );
            return Number(idResCliente) === Number(clienteId);
          });
          console.log(
            "Cantidad de reservas filtradas para clienteId",
            clienteId,
            ":",
            reservasFiltradas.length
          );
          setReservasCliente(reservasFiltradas);
        } else {
          console.error("Formato inesperado en response.data:", response.data);
        }
      })
      .catch((error) => {
        console.error("Error al obtener reservas:", error);
      });
  }, [clienteId]);

  return (
    <div>
      <h3>Reservas para Cliente {clienteId}</h3>
      <p>Cantidad de reservas: {reservasCliente.length}</p>
      <ul>
        {reservasCliente.map((reserva) => (
          <li key={reserva.idReserva}>
            Reserva #{reserva.idReserva} – Fecha: {reserva.fechaReserva} – Hora:{" "}
            {reserva.horaInicio}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ReservasCliente;

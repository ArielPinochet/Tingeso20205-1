import axios from "axios";

const API_URL = "http://localhost:8080/app";

const RESERVA_URL = `${API_URL}/reservas`;

const obtenerReservas = () => axios.get(RESERVA_URL);
const obtenerReservaPorId = (id) => axios.get(`${RESERVA_URL}/${id}`);
const crearReserva = (reserva) => {
  // Construye los params para la URL, SIN precioTotal
  const params = new URLSearchParams({
    nombreCliente: reserva.nombreCliente,
    fechaReserva: reserva.fechaReserva,
    horaInicio: reserva.horaInicio,
    numeroVueltas: reserva.numeroVueltas,
    cantidadPersonas: reserva.cantidadPersonas,
    diaEspecial: reserva.diaEspecial
  }).toString();

  return axios.post(`${RESERVA_URL}?${params}`);
};
const actualizarReserva = (id, reserva) => axios.put(`${RESERVA_URL}/${id}`, reserva);
const eliminarReserva = (id) => axios.delete(`${RESERVA_URL}/${id}`);
export const obtenerReservasPorCliente = (clienteId) => {
    return axios.get(`/api/reservas/cliente/${clienteId}`);
  };

export { obtenerReservas, obtenerReservaPorId, crearReserva, actualizarReserva, eliminarReserva };

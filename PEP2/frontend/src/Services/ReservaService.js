import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8090";

const RESERVA_URL = `${API_URL}/reservas`;

const obtenerReservas = () => axios.get(RESERVA_URL);
const obtenerReservaPorId = (id) => axios.get(`${RESERVA_URL}/${id}`);
const crearReserva = (reserva) => axios.post(RESERVA_URL, reserva);
const actualizarReserva = (id, reserva) => axios.put(`${RESERVA_URL}/${id}`, reserva);
const eliminarReserva = (id) => axios.delete(`${RESERVA_URL}/${id}`);
export const obtenerReservasPorCliente = (clienteId) => {
    return axios.get(`/api/reservas/cliente/${clienteId}`);
  };

export { obtenerReservas, obtenerReservaPorId, crearReserva, actualizarReserva, eliminarReserva };

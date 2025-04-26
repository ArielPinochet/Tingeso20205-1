import axios from "axios";

const API_URL = "http://localhost:8080/reservas";

const obtenerReservas = () => axios.get(API_URL);
const obtenerReservaPorId = (id) => axios.get(`${API_URL}/${id}`);
const crearReserva = (reserva) => axios.post(API_URL, reserva);
const actualizarReserva = (id, reserva) => axios.put(`${API_URL}/${id}`, reserva);
const eliminarReserva = (id) => axios.delete(`${API_URL}/${id}`);
export const obtenerReservasPorCliente = (clienteId) => {
    return axios.get(`/api/reservas/cliente/${clienteId}`);
  };

export { obtenerReservas, obtenerReservaPorId, crearReserva, actualizarReserva, eliminarReserva };

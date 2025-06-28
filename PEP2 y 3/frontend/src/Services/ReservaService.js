import axios from "axios";

const API_URL = "http://localhost:8080/app";
const RESERVA_URL = `${API_URL}/reservas`;

const obtenerReservas = () => axios.get(RESERVA_URL);
const obtenerReservaPorId = (id) => axios.get(`${RESERVA_URL}/${id}`);

// Modificado para enviar los carros como parte de los parámetros de la URL
const crearReserva = (reserva) => {
  // Construye los params para la URL, incluyendo codigosCarros como múltiples parámetros
  const params = new URLSearchParams({
    nombreCliente: reserva.nombreCliente,
    fechaReserva: reserva.fechaReserva,
    horaInicio: reserva.horaInicio,
    numeroVueltas: reserva.numeroVueltas,
    cantidadPersonas: reserva.cantidadPersonas,
    diaEspecial: reserva.diaEspecial
  });

  // Agrega cada código de carro como un parámetro codigosCarros
  if (Array.isArray(reserva.codigosCarros)) {
    reserva.codigosCarros.forEach((codigo) => {
      params.append("codigosCarros", codigo);
    });
  }

  return axios.post(`${RESERVA_URL}?${params.toString()}`);
};

const actualizarReserva = (idReserva, reserva) => {
  const params = new URLSearchParams({
    idReserva,
    nombreCliente: reserva.nombreCliente,
    fechaReserva: reserva.fechaReserva,
    horaInicio: reserva.horaInicio,
    numeroVueltas: reserva.numeroVueltas,
    cantidadPersonas: reserva.cantidadPersonas,
    diaEspecial: reserva.diaEspecial
  });

  if (Array.isArray(reserva.codigosCarros)) {
    reserva.codigosCarros.forEach((codigo) => {
      params.append("codigosCarros", codigo);
    });
  }

  // PUT con params en la URL
  return axios.put(`${RESERVA_URL}/editar?${params.toString()}`);
};
const eliminarReserva = (id) => axios.delete(`${RESERVA_URL}/${id}`);
export const obtenerReservasPorCliente = (clienteId) => {
  return axios.get(`/api/reservas/cliente/${clienteId}`);
};

export { obtenerReservas, obtenerReservaPorId, crearReserva, actualizarReserva, eliminarReserva };

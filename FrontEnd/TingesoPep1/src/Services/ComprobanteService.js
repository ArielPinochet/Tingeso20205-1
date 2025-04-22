import axios from "axios";

const API_URL = "http://localhost:8080/comprobante-pago";

const obtenerComprobantes = () => axios.get(API_URL);
const obtenerComprobantePorId = (id) => axios.get(`${API_URL}/${id}`);
const crearComprobante = (comprobante) => axios.post(API_URL, comprobante);
const eliminarComprobante = (id) => axios.delete(`${API_URL}/${id}`);

export { obtenerComprobantes, obtenerComprobantePorId, crearComprobante, eliminarComprobante };

import axios from "axios";

const API_URL = "http://localhost:8080/Carros";

const obtenerCarros = () => axios.get(API_URL);
const obtenerCarroPorId = (id) => axios.get(`${API_URL}/${id}`);
const crearCarro = (carro) => axios.post(API_URL, carro);
const actualizarCarro = (id, carro) => axios.put(`${API_URL}/${id}`, carro);
const eliminarCarro = (id) => axios.delete(`${API_URL}/${id}`);

export { obtenerCarros, obtenerCarroPorId, crearCarro, actualizarCarro, eliminarCarro };

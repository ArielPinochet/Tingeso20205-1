import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://backend-lb:8090";


const CARRO_URL = `${API_URL}/Carros`;

const obtenerCarros = () => axios.get(CARRO_URL);
const obtenerCarroPorId = (id) => axios.get(`${CARRO_URL}/${id}`);
const crearCarro = (carro) => axios.post(CARRO_URL, carro);
const actualizarCarro = (id, carro) => axios.put(`${CARRO_URL}/${id}`, carro);
const eliminarCarro = (id) => axios.delete(`${CARRO_URL}/${id}`);

export { obtenerCarros, obtenerCarroPorId, crearCarro, actualizarCarro, eliminarCarro };

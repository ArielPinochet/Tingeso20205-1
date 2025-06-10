import axios from "axios";

const API_URL = "http://localhost:8080";



const CARRO_URL = `${API_URL}/app/carros`;

const obtenerCarros = () => axios.get(CARRO_URL);
const obtenerCarroPorId = (id) => axios.get(`${CARRO_URL}/${id}`);
const crearCarro = async (carro) => {
    try {
        console.log("🔹 Enviando carro:", carro); 
        const response = await axios.post(CARRO_URL, carro, {
            headers: {
                "Content-Type": "application/json",
            }
        });
        return response.data;
    } catch (error) {
        console.error("🚨 Error al enviar carro:", error);
        throw error;
    }
};

const actualizarCarro = (id, carro) => axios.put(`${CARRO_URL}/${id}`, carro);
const eliminarCarro = (id) => axios.delete(`${CARRO_URL}/${id}`);

export { obtenerCarros, obtenerCarroPorId, crearCarro, actualizarCarro, eliminarCarro };

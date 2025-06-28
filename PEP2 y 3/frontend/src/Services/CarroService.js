import axios from "axios";

const API_URL = "http://localhost:8080";



const CARRO_URL = `${API_URL}/app/carros`;

const obtenerCarros = () => axios.get(CARRO_URL);
const obtenerCarroPorId = (id) => axios.get(`${CARRO_URL}/${id}`);
const crearCarro = async (codigoCarros, modelo, estado) => {
    try {
        console.log("🔹 Enviando carro con fetch:", { codigoCarros, modelo, estado });

        const response = await fetch(`${CARRO_URL}?codigoCarros=${encodeURIComponent(codigoCarros)}&modelo=${encodeURIComponent(modelo)}&estado=${encodeURIComponent(estado)}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        });

        if (!response.ok) {
            console.error("🚨 Error en la respuesta:", response.status);
            const errorData = await response.text();
            console.error("🔹 Detalles del error:", errorData);
            throw new Error(`Error ${response.status}: ${errorData}`);
        }

        const data = await response.json();
        console.log("✔️ Respuesta del servidor:", data);
        return data;
    } catch (error) {
        console.error("🚨 Error al enviar carro con fetch:", error);
        throw error;
    }
};

export const obtenerCarrosOcupados = async (fecha, hora) => {
  const response = await axios.get(`http://localhost:8080/app/carros-ocupados?fecha=${fecha}&hora=${hora}`);
  console.log("Carros ocupados para", fecha, hora, ":", response.data);
  return response;
};



const actualizarCarro = (id, carro) => axios.put(`${CARRO_URL}/${id}`, carro);

const eliminarCarro = async (codigo) => {
    try {
        console.log(`🔹 Eliminando carro con código: ${codigo}`);
        
        const response = await axios.delete(`${CARRO_URL}/${encodeURIComponent(codigo)}`);

        console.log("✔️ Carro eliminado con éxito");
        return response.data;
    } catch (error) {
        console.error("🚨 Error al eliminar carro:", error);
        
        if (error.response) {
            console.error("🔹 Detalles del error:", error.response.data);
            console.error("🔹 Código de estado:", error.response.status);
        } else {
            console.error("🔹 No hay respuesta del servidor.");
        }

        throw error;
    }
};


export { obtenerCarros, obtenerCarroPorId, crearCarro, actualizarCarro, eliminarCarro };

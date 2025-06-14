import axios from "axios";

const API_URL = "http://localhost:8080/app/comprobantes";


const obtenerComprobantes = () => axios.get(API_URL);
const obtenerComprobantePorId = (id) => axios.get(`${API_URL}/${id}`);
export const crearComprobante = async (formData) => {
    const response = await fetch(API_URL, {
      method: "POST",
      body: formData,
    });
    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData);
    }
    return response.json();
  };

const eliminarComprobante = (id) => axios.delete(`${API_URL}/${id}`);

export { obtenerComprobantes, obtenerComprobantePorId, eliminarComprobante };

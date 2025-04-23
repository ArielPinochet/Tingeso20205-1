import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { crearReserva, actualizarReserva, obtenerReservaPorId } from "../Services/ReservaService";
import { obtenerClientes } from "../Services/ClientService";
import { obtenerCarros } from "../Services/CarroService";
import { useNavigate, useParams } from "react-router-dom";

const FormularioReserva = () => {
  const [reserva, setReserva] = useState({
    fechaReserva: new Date(),
    horaInicio: "",
    numeroVueltas: 10,
    cantidadPersonas: "",
    diaEspecial: false,
    estadoReserva: "Pendiente", // Estado fijo
    precioTotal: 0,
    duracionTotal: 30,
    idClienteResponsable: "",
    clientes: [],
    carrosAsignados: {} // Mapeo cliente -> carro
  });

  const [clientes, setClientes] = useState([]);
  const [carros, setCarros] = useState([]);
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    obtenerClientes().then((response) => setClientes(response.data));
    obtenerCarros().then((response) => setCarros(response.data));

    if (id) {
      obtenerReservaPorId(id).then((response) => setReserva(response.data));
    }
  }, [id]);

  // Cambiar duración y precio según las vueltas
  const handleVueltasChange = (e) => {
    const vueltas = parseInt(e.target.value);
    let duracion, precioBase;

    switch (vueltas) {
      case 10:
        duracion = 30;
        precioBase = 15000;
        break;
      case 15:
        duracion = 35;
        precioBase = 20000;
        break;
      case 20:
        duracion = 40;
        precioBase = 25000;
        break;
      default:
        duracion = 30;
        precioBase = 15000;
    }

    setReserva({
      ...reserva,
      numeroVueltas: vueltas,
      duracionTotal: duracion,
      precioTotal: precioBase * reserva.cantidadPersonas
    });
  };

  // Calcula el precio total al modificar la cantidad de personas
  const handlePersonasChange = (e) => {
    const cantidad = parseInt(e.target.value);
    setReserva({
      ...reserva,
      cantidadPersonas: cantidad,
      precioTotal:
        cantidad *
        (reserva.numeroVueltas === 10
          ? 15000
          : reserva.numeroVueltas === 15
          ? 20000
          : 25000)
    });
  };

  // Asignación de carros a clientes sin repetirse
  const handleCarroAsignadoChange = (index, carroId) => {
    if (Object.values(reserva.carrosAsignados).includes(carroId)) {
      alert("Este carro ya fue seleccionado. Escoge otro.");
      return;
    }

    setReserva({
      ...reserva,
      carrosAsignados: { ...reserva.carrosAsignados, [index]: carroId }
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
  
    // Convertir reserva.idClienteResponsable a número para la comparación
    const clienteSeleccionado = clientes.find(
      (cliente) => cliente.idCliente === Number(reserva.idClienteResponsable)
    );
    if (!clienteSeleccionado) {
      alert("Debes seleccionar un cliente válido.");
      return;
    }
  
    // Convertir los carros asignados (un objeto) a una lista de objetos completos
    const carrosSeleccionados = Object.values(reserva.carrosAsignados || {}).map(codigoCarro => {
        return carros.find(carro => carro.codigoCarros === codigoCarro);
    }).filter(carro => carro !== undefined);
    
  
    // Construir el objeto final de reserva
    const reservaFinal = {
      fechaReserva: reserva.fechaReserva,
      horaInicio: reserva.horaInicio,
      numeroVueltas: reserva.numeroVueltas,
      cantidadPersonas: reserva.cantidadPersonas,
      diaEspecial: reserva.diaEspecial,
      estadoReserva: reserva.estadoReserva,
      precioTotal: reserva.precioTotal,
      duracionTotal: reserva.duracionTotal,
      clienteResponsable: clienteSeleccionado, // Enviamos la entidad completa del cliente
      carros: carrosSeleccionados // Enviamos la lista de carros como objetos completos
    };
  
    console.log("Datos enviados al backend:", reservaFinal);
  
    if (id) {
      actualizarReserva(id, reservaFinal).then(() => navigate("/reservas"));
    } else {
      crearReserva(reservaFinal).then(() => navigate("/reservas"));
    }
  };
  

  return (
    <div className="container mt-4">
      <h2>{id ? "Editar Reserva" : "Crear Reserva"}</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Fecha de Reserva</label>
          <DatePicker
            selected={reserva.fechaReserva}
            onChange={(date) =>
              setReserva({
                ...reserva,
                fechaReserva: date.toISOString().split("T")[0]
              })
            }
            className="form-control"
            dateFormat="yyyy-MM-dd"
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Hora de Inicio</label>
          <input
            type="time"
            className="form-control"
            name="horaInicio"
            value={reserva.horaInicio}
            onChange={(e) =>
              setReserva({ ...reserva, horaInicio: e.target.value })
            }
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Número de Vueltas</label>
          <select
            className="form-select"
            name="numeroVueltas"
            value={reserva.numeroVueltas}
            onChange={handleVueltasChange}
            required
          >
            <option value="10">10 vueltas - 30 min</option>
            <option value="15">15 vueltas - 35 min</option>
            <option value="20">20 vueltas - 40 min</option>
          </select>
        </div>
        <div className="mb-3">
          <label className="form-label">Cantidad de Personas</label>
          <input
            type="number"
            className="form-control"
            name="cantidadPersonas"
            value={reserva.cantidadPersonas}
            onChange={handlePersonasChange}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Día Especial</label>
          <select
            className="form-select"
            name="diaEspecial"
            value={reserva.diaEspecial}
            onChange={(e) =>
              setReserva({ ...reserva, diaEspecial: e.target.value })
            }
            required
          >
            <option value="false">No</option>
            <option value="true">Sí</option>
          </select>
        </div>
        <div className="mb-3">
          <label className="form-label">Cliente Responsable</label>
          <select
            className="form-select"
            name="idClienteResponsable"
            value={reserva.idClienteResponsable}
            onChange={(e) =>
              setReserva({ ...reserva, idClienteResponsable: e.target.value })
            }
            required
          >
            <option value="">Selecciona un cliente</option>
            {clientes.map((cliente) => (
              <option key={cliente.idCliente} value={cliente.idCliente}>
                {cliente.nombre}
              </option>
            ))}
          </select>
        </div>
        {/* Selección de carros según la cantidad de personas */}
        {Array.from({ length: reserva.cantidadPersonas }, (_, index) => (
          <div className="mb-3" key={index}>
            <label className="form-label">Seleccione Carro {index + 1}</label>
            <select
              className="form-select"
              onChange={(e) => handleCarroAsignadoChange(index, e.target.value)}
              required
            >
              <option value="">Selecciona un carro</option>
              {carros.map((carro) => (
                <option key={carro.idCarro} value={carro.codigoCarros}>
                  {carro.codigoCarros}
                </option>
              ))}
            </select>
          </div>
        ))}
        <button type="submit" className="btn btn-success">
          {id ? "Actualizar" : "Guardar"}
        </button>
        <button
          type="button"
          className="btn btn-secondary ms-2"
          onClick={() => navigate("/reservas")}
        >
          Cancelar
        </button>
      </form>
    </div>
  );
};

export default FormularioReserva;

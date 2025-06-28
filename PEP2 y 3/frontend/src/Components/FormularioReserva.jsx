import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { 
  crearReserva, 
  actualizarReserva, 
  obtenerReservaPorId,
  obtenerReservas  // función para obtener todas las reservas
} from "../Services/ReservaService";
import { obtenerClientes } from "../Services/ClientService";
import { obtenerCarros, obtenerCarrosOcupados } from "../Services/CarroService";
import { useNavigate, useParams } from "react-router-dom";

/*
 * Esta función espera que dateStr sea "YYYY-MM-DD" que representa la fecha en UTC.
 * Se crea un objeto Date en UTC y se convierte a la fecha local sin hora (T00:00:00 local).
 *
 * Ajusta el desfase si fuera necesario (por ejemplo, sumando un día si tu zona horaria es positiva).
 */
const convertUTCToLocal = (dateStr) => {
  // Crear fecha en UTC a partir del string
  const [year, month, day] = dateStr.split("-");
  // Usamos Date.UTC para asegurarnos de interpretar la fecha como UTC.
  const dt = new Date(Date.UTC(Number(year), Number(month) - 1, Number(day)+ 1));
  // Ahora, convertimos a la fecha local. Por ejemplo, obtenemos las partes locales:
  const localYear = dt.getFullYear();
  // Nota: Si ves que al convertir la fecha te falta 1 día, puedes ajustar sumando uno.
  const localMonth = ("0" + (dt.getMonth() + 1)).slice(-2);
  const localDay = ("0" + dt.getDate()).slice(-2);
  return `${localYear}-${localMonth}-${localDay}`;
};

/*
 * Una función similar para asegurarse de formatear una Date
 * al formato "YYYY-MM-DD" en horario local.
 */
const formatLocalDate = (dateObj) => {
  const year = dateObj.getFullYear();
  const month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
  const day = ("0" + dateObj.getDate()).slice(-2);
  return `${year}-${month}-${day}`;
};


const FormularioReserva = () => {
  const [reserva, setReserva] = useState({
    fechaReserva: formatLocalDate(new Date()),  // Guardamos la fecha en formato "YYYY-MM-DD"
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
  // Estado para almacenar las reservas existentes para filtrar karts ya reservados.
  const [reservasExistentes, setReservasExistentes] = useState([]);
  const [guardando, setGuardando] = useState(false);
  const [reservaGuardada, setReservaGuardada] = useState(false);

  const navigate = useNavigate();
  const { id } = useParams();

  // Cargar clientes, carros y, si es edición, la reserva por ID
  useEffect(() => {
    obtenerClientes().then((response) => setClientes(response.data));
    obtenerCarros().then((response) => setCarros(response.data));
    if (id) {
      obtenerReservaPorId(id).then((response) => {
        const data = response.data;
        // Si la fecha viene como "2025-06-27T22:46:00", sepárala
        let fecha = data.fechaReserva;
        let hora = data.horaInicio;
        if (fecha && fecha.includes("T")) {
          const [f, h] = fecha.split("T");
          fecha = f;
          hora = h ? h.substring(0, 5) : ""; // "22:46"
        }
        // Asegura que carrosAsignados siempre sea un objeto con índices como claves
        let carrosAsignados = {};
        if (Array.isArray(data.codigosCarros)) {
          data.codigosCarros.forEach((codigo, idx) => {
            carrosAsignados[idx] = codigo;
          });
        }
        setReserva({
          ...data,
          fechaReserva: fecha,
          horaInicio: hora,
          carrosAsignados: carrosAsignados,
          cantidadPersonas: data.cantidadPersonas || (data.codigosCarros ? data.codigosCarros.length : 1)
        });
      });
    }
  }, [id]);

  // Cada vez que cambia la fecha u hora, obtén las reservas existentes desde la API.
  useEffect(() => {
    // Solo consulta si hay fecha y hora seleccionadas
    if (reserva.fechaReserva && reserva.horaInicio) {
      // Llama al endpoint de carros ocupados
      obtenerCarrosOcupados(reserva.fechaReserva, reserva.horaInicio)
        .then((response) => {
          // response.data es un array de códigos de carros ocupados
          setReservasExistentes(response.data); // Ahora reservasExistentes es un array de códigos ocupados
        })
        .catch((error) => {
          console.error("Error al obtener carros ocupados:", error);
          setReservasExistentes([]);
        });
    }
  }, [reserva.fechaReserva, reserva.horaInicio]);

  // Actualiza duración y precio según el número de vueltas
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

  // Actualiza precio total cuando cambia la cantidad de personas
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

  // Asignación de carros a cada persona sin repetirse
  const handleCarroAsignadoChange = (index, carroId) => {
    if (Object.values(reserva.carrosAsignados).includes(carroId)) {
      alert("Este carro ya fue seleccionado. Escoge otro.");
      // 🔹 Deselecciona el carro para este índice
      setReserva({
        ...reserva,
        carrosAsignados: { ...reserva.carrosAsignados, [index]: "" }
      });
      return;
    }
    setReserva({
      ...reserva,
      carrosAsignados: { ...reserva.carrosAsignados, [index]: carroId }
    });
  };

  // Función para saber si la fecha/hora seleccionada ya pasó
  const esFechaHoraPasada = () => {
    if (!reserva.fechaReserva || !reserva.horaInicio) return false;
    const ahora = new Date();
    const [anio, mes, dia] = reserva.fechaReserva.split("-");
    const [hora, minuto] = reserva.horaInicio.split(":");
    const fechaSeleccionada = new Date(anio, mes - 1, dia, hora, minuto);
    return fechaSeleccionada < ahora;
  };

  // Función para saber si la hora está fuera del horario permitido (22:00 a 06:00)
  const esHoraFueraDeHorario = () => {
    if (!reserva.horaInicio) return false;
    const [hora, minuto] = reserva.horaInicio.split(":").map(Number);
    // Si la hora es >= 22 o < 6, está fuera de horario
    return hora >= 22 || hora < 6;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (esFechaHoraPasada()) {
      alert("No puedes reservar una fecha/hora que ya pasó.");
      return;
    }

    if (esHoraFueraDeHorario()) {
      alert("No puedes reservar fuera del horario de atención (06:00 a 21:59).");
      return;
    }

    // 🔹 Filtrar carros disponibles para la fecha/hora seleccionada
    const carrosDisponibles = carros.filter(
      (carro) =>
        !carro.enMantenimiento &&
        !reservasExistentes.includes(carro.codigoCarros)
    );

    if (reserva.cantidadPersonas > carrosDisponibles.length) {
      alert("No hay carros suficientes para la cantidad de personas seleccionada.");
      return;
    }

    const clienteSeleccionado = clientes.find(
      (cliente) => cliente.idCliente === Number(reserva.idClienteResponsable)
    );
    if (!clienteSeleccionado) {
      alert("Debes seleccionar un cliente válido.");
      return;
    }

    if (!reserva.fechaReserva || !reserva.horaInicio) {
      alert("Debes seleccionar fecha y hora de inicio.");
      return;
    }

    const fechaHora = `${reserva.fechaReserva}T${reserva.horaInicio}:00`;

    // 🔹 Obtener los códigos de carros seleccionados como array de string
    const codigosCarros = Object.values(reserva.carrosAsignados)
      .filter(Boolean)
      .map(String);

    const reservaParams = {
      nombreCliente: clienteSeleccionado.nombre,
      fechaReserva: fechaHora,
      horaInicio: fechaHora,
      numeroVueltas: reserva.numeroVueltas,
      cantidadPersonas: reserva.cantidadPersonas,
      diaEspecial: reserva.diaEspecial === "true" || reserva.diaEspecial === true,
      codigosCarros // 🔹 Enviar como List<String>
    };

    console.log("Params enviados al backend:", reservaParams);

    setGuardando(true); // Mostrar modal de guardando

    if (id) {
      // Si hay id, actualiza la reserva existente
      actualizarReserva(id, reservaParams)
        .then(() => {
          setGuardando(false);
          setReservaGuardada(true);
        })
        .catch(() => {
          setGuardando(false);
          alert("Ocurrió un error al actualizar la reserva.");
        });
    } else {
      // Si no hay id, crea una nueva reserva
      crearReserva(reservaParams)
        .then(() => {
          setGuardando(false);
          setReservaGuardada(true); // Mostrar modal de éxito
        })
        .catch(() => {
          setGuardando(false);
          alert("Ocurrió un error al guardar la reserva.");
        });
    }
  };

  return (
    <div className="container mt-4">
      {/* Modal de guardando */}
      {guardando && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 3000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "10px",
            boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
          }}>
            <h5>Guardando reserva...</h5>
            <div className="spinner-border text-primary mt-3" role="status">
              <span className="visually-hidden">Guardando...</span>
            </div>
          </div>
        </div>
      )}

      {/* Modal de reserva guardada */}
      {reservaGuardada && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 3000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "10px",
            boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
          }}>
            <h5>¡Reserva guardada!</h5>
            <button className="btn btn-success mt-3" onClick={() => {
              setReservaGuardada(false);
              navigate("/reservas");
            }}>
              OK
            </button>
          </div>
        </div>
      )}

      <h2>{id ? "Editar Reserva" : "Crear Reserva"}</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Fecha de Reserva</label>
          {/* Para el DatePicker usamos la fecha formateada y la sumamos manualmente si es necesaria */}
          <DatePicker
            selected={
              reserva.fechaReserva && /^\d{4}-\d{2}-\d{2}$/.test(reserva.fechaReserva)
                ? new Date(reserva.fechaReserva + "T00:00:00")
                : new Date()
            }
            onChange={(date) =>
              setReserva({
                ...reserva,
                fechaReserva: formatLocalDate(date)
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
          <label className="form-label">Es el cumpleaños de alguno?</label>
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
            <label className="form-label">
              Seleccione Carro {index + 1}
            </label>
            <select
  className="form-select"
  value={reserva.carrosAsignados[index] || ""}
  onChange={(e) => handleCarroAsignadoChange(index, e.target.value)}
  required
>
  <option value="">Selecciona un carro</option>
  {carros
    .filter((carro) => {
      // 1. Excluir carros en mantenimiento.
      if (carro.enMantenimiento) return false;
      // 2. Excluir carros ocupados en la hora seleccionada
      if (reservasExistentes.includes(carro.codigoCarros)) return false;
      return true;
    })
    .sort((a, b) =>
      parseInt(a.codigoCarros.replace(/\D/g, "")) -
      parseInt(b.codigoCarros.replace(/\D/g, ""))
    )
    .map((carro) => (
      <option key={carro.idCarro} value={carro.codigoCarros}>
        {carro.codigoCarros}
      </option>
    ))}
</select>



          </div>
        ))}
        <button
          type="submit"
          className="btn btn-success"
          disabled={esFechaHoraPasada() || esHoraFueraDeHorario()}
        >
          {id ? "Actualizar" : "Guardar"}
        </button>
        {esFechaHoraPasada() && (
          <div className="alert alert-warning mt-2">
            No puedes reservar una fecha/hora que ya pasó.
          </div>
        )}
        {esHoraFueraDeHorario() && (
          <div className="alert alert-warning mt-2">
            El horario de reservas es de 06:00 a 21:59.
          </div>
        )}
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

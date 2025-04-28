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
import { obtenerCarros } from "../Services/CarroService";
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

  const navigate = useNavigate();
  const { id } = useParams();

  // Cargar clientes, carros y, si es edición, la reserva por ID
  useEffect(() => {
    obtenerClientes().then((response) => setClientes(response.data));
    obtenerCarros().then((response) => setCarros(response.data));
    if (id) {
      obtenerReservaPorId(id).then((response) => {
        // Suponiendo que la reserva que viene tiene fechaReserva en "YYYY-MM-DD" ya ajustada
        setReserva(response.data);
      });
    }
  }, [id]);

  // Cada vez que cambia la fecha u hora, obtén las reservas existentes desde la API.
  useEffect(() => {
    if (reserva.fechaReserva && reserva.horaInicio) {
      obtenerReservas()
        .then((response) => {
          const data = Array.isArray(response.data)
            ? response.data
            : response.data.reservas || [];
          console.log("Todas las reservas recibidas:", data);
          // Para comparar, convertimos la fecha de la API a fecha local usando convertUTCToLocal.
          const filtradas = data.filter((r) => {
            return convertUTCToLocal(r.fechaReserva) === reserva.fechaReserva;
        });
        console.log("Reservas existentes filtradas para el día:", filtradas);
        setReservasExistentes(filtradas);
        })
        .catch((error) =>
          console.error("Error al obtener reservas para filtrar:", error)
        );
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
      return;
    }
    setReserva({
      ...reserva,
      carrosAsignados: { ...reserva.carrosAsignados, [index]: carroId }
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const clienteSeleccionado = clientes.find(
      (cliente) => cliente.idCliente === Number(reserva.idClienteResponsable)
    );
    if (!clienteSeleccionado) {
      alert("Debes seleccionar un cliente válido.");
      return;
    }
    // Sumar 1 día a la fecha de reserva para corregir el desfase
    const correctedFechaReserva = (() => {
      // Se asume que reserva.fechaReserva ya viene en formato "YYYY-MM-DD"
      const originalDate = new Date(reserva.fechaReserva + "T00:00:00");
      const newDate = new Date(originalDate.getTime() + 24 * 60 * 60 * 1000);
      return formatLocalDate(newDate);
    })();
    const carrosSeleccionados = Object.values(reserva.carrosAsignados || {})
      .map((codigoCarro) => {
        return carros.find((carro) => carro.codigoCarros === codigoCarro);
      })
      .filter((carro) => carro !== undefined);
    const reservaFinal = {
      fechaReserva: reserva.fechaReserva,
      horaInicio: reserva.horaInicio,
      numeroVueltas: reserva.numeroVueltas,
      cantidadPersonas: reserva.cantidadPersonas,
      diaEspecial: reserva.diaEspecial,
      estadoReserva: reserva.estadoReserva,
      precioTotal: reserva.precioTotal,
      duracionTotal: reserva.duracionTotal,
      clienteResponsable: clienteSeleccionado,
      carros: carrosSeleccionados
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
          {/* Para el DatePicker usamos la fecha formateada y la sumamos manualmente si es necesaria */}
          <DatePicker
            selected={reserva.fechaReserva ? new Date(reserva.fechaReserva + "T00:00:00") : new Date()}
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
          <label className="form-label">Día Especial (CUMPLEAÑOS DE ALGUNO?)</label>
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
  onChange={(e) => handleCarroAsignadoChange(index, e.target.value)}
  required
>
  <option value="">Selecciona un carro</option>
  {carros
    .filter((carro) => {
      // 1. Excluir carros en mantenimiento.
      if (carro.enMantenimiento) return false;

      // 2. Si hay fecha, hora y duración definidos, evaluamos la superposición de horarios.
      if (reserva.fechaReserva && reserva.horaInicio && reserva.duracionTotal) {
        // Obtenemos la hora del input, por ejemplo "11:11", y añadimos segundos para construir el Date.
        const newTime = reserva.horaInicio.length >= 5 ? reserva.horaInicio.substr(0, 5) : reserva.horaInicio;
        const newResStart = new Date(`${reserva.fechaReserva}T${newTime}:00`);
        const newResEnd = new Date(newResStart.getTime() + reserva.duracionTotal * 60000);

        // Revisamos todas las reservas existentes para ese día.
        const isReserved = reservasExistentes.some((r) => {
          // La hora de la reserva existente (normalizamos a "HH:mm")
          const existingTime = r.horaInicio && r.horaInicio.length >= 5 
            ? r.horaInicio.substr(0, 5) 
            : r.horaInicio;

          // Convertir la fecha de la API (en UTC) a local.
          const existingDate = convertUTCToLocal(r.fechaReserva);
          const existingStart = new Date(`${existingDate}T${existingTime}:00`);
          const existingEnd = new Date(existingStart.getTime() + r.duracionTotal * 60000);

          console.log(
            `Comparando nueva reserva [${newResStart.toLocaleTimeString()} - ${newResEnd.toLocaleTimeString()}] con existente [${existingStart.toLocaleTimeString()} - ${existingEnd.toLocaleTimeString()}]`
          );

          // Verificar si hay solapamiento de intervalos.
          const overlap = newResStart < existingEnd && existingStart < newResEnd;
          
          if (overlap) {
            // Construimos un array con los códigos de carro reservados en la reserva existente.
            let assignedCarCodes = [];
            if (r.kartId) {
              assignedCarCodes.push(r.kartId.toString());
            }
            if (r.carros && Array.isArray(r.carros) && r.carros.length > 0) {
              // Concatenamos todos los códigos de carro presentes.
              assignedCarCodes = assignedCarCodes.concat(
                r.carros.map((c) => c.codigoCarros)
              );
            }
            // Registramos en consola para ver la comparación.
            console.log(
              `Reserva existente tiene asignado: [${assignedCarCodes.join(", ")}]. Evaluando carro: ${carro.codigoCarros}`
            );
            // Se marca reservado si el array incluye el código del carro en evaluación.
            return assignedCarCodes.includes(carro.codigoCarros);
          }
          return false;
        });
        if (isReserved) {
          console.log(`El carro ${carro.codigoCarros} está reservado en este intervalo.`);
          return false;
        }
      }
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

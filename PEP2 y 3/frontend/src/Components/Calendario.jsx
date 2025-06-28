import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es'; 
import axios from 'axios';
import { obtenerReservaPorId } from '../Services/ReservaService';

const CALENDARIO_URL = "http://localhost:8080/api/calendario/todos";

const Calendario = () => {
  const [eventos, setEventos] = useState([]);
  const [eventoSeleccionado, setEventoSeleccionado] = useState(null);
  const [detalleReserva, setDetalleReserva] = useState(null);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);

  useEffect(() => {
    axios.get(CALENDARIO_URL)
      .then((response) => {
        const data = Array.isArray(response.data) ? response.data : [];
        const eventosProcesados = data.map((item) => {
          const start = `${item.fecha}T${item.horaInicio}`;
          const end = `${item.fecha}T${item.horaFin}`;
          return {
            ...item,
            id: item.id,
            title: `Reserva ${item.reservaId} - ${item.clienteNombre}`,
            start,
            end,
            color: item.estado === "RESERVADO" ? "#28a745" : "#dc3545"
          };
        });
        setEventos(eventosProcesados);
      })
      .catch((error) => {
        console.error("Error al obtener calendario:", error);
      });
  }, []);

  useEffect(() => {
    if (eventoSeleccionado && eventoSeleccionado.reservaId) {
      setCargandoDetalle(true);
      obtenerReservaPorId(eventoSeleccionado.reservaId)
        .then((res) => {
          setDetalleReserva(res.data);
          setCargandoDetalle(false);
        })
        .catch(() => {
          setDetalleReserva(null);
          setCargandoDetalle(false);
        });
    } else {
      setDetalleReserva(null);
    }
  }, [eventoSeleccionado]);

  // Agrega el estilo para el cursor pointer sobre los eventos
  useEffect(() => {
    const style = document.createElement("style");
    style.innerHTML = `
      .fc-event, .fc-v-event, .fc-h-event {
        cursor: pointer !important;
      }
    `;
    document.head.appendChild(style);
    return () => {
      document.head.removeChild(style);
    };
  }, []);

  return (
    <div>
      <h2>Calendario de Ocupación de la Pista</h2>
      <div className="alert alert-info py-2 mb-3" style={{fontSize: "1rem"}}>
        Click en la reserva para más detalles
      </div>
      <FullCalendar
        plugins={[timeGridPlugin, interactionPlugin]}
        initialView="timeGridWeek"
        locale={esLocale}
        headerToolbar={{
          left: "prev,next today",
          center: "title",
          right: "timeGridWeek,timeGridDay"
        }}
        allDaySlot={false}
        slotMinTime="05:00:00"
        slotMaxTime="23:00:00"
        events={eventos}
        height="auto"
        eventClick={(info) => {
          setEventoSeleccionado(info.event.extendedProps);
        }}
      />

      {/* Modal de detalle de reserva */}
      {eventoSeleccionado && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 4000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "10px",
            boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "340px", maxWidth: "90vw"
          }}>
            <h4>Detalle de Reserva</h4>
            {cargandoDetalle && (
              <div className="text-center my-3">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Cargando...</span>
                </div>
                <div>Cargando detalles...</div>
              </div>
            )}
            {detalleReserva && (
              <ul style={{ listStyle: "none", padding: 0 }}>
                <li><b>ID Reserva:</b> {detalleReserva.idReserva}</li>
                <li><b>Cliente:</b> {detalleReserva.nombreCliente}</li>
                <li>
                  <b>Fecha:</b>{" "}
                  {detalleReserva.fechaReserva
                    ? new Date(detalleReserva.fechaReserva).toLocaleDateString("es-CL", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                      })
                    : ""}
                </li>
                <li>
                  <b>Hora Inicio:</b>{" "}
                  {detalleReserva.horaInicio
                    ? new Date(detalleReserva.horaInicio).toLocaleTimeString("es-CL", {
                        hour: "2-digit",
                        minute: "2-digit"
                      })
                    : ""}
                </li>
                <li><b>Número de Vueltas:</b> {detalleReserva.numeroVueltas}</li>
                <li><b>Cantidad de Personas:</b> {detalleReserva.cantidadPersonas}</li>
                <li><b>Día Especial:</b> {detalleReserva.diaEspecial ? "Sí" : "No"}</li>
                <li><b>Carros:</b> {detalleReserva.carros && detalleReserva.carros.length > 0 ? detalleReserva.carros.join(", ") : "No asignados"}</li>
              </ul>
            )}
            {!cargandoDetalle && !detalleReserva && (
              <div className="text-danger">No se pudo cargar el detalle de la reserva.</div>
            )}
            <div className="d-flex justify-content-end">
              <button className="btn btn-primary mt-2" onClick={() => setEventoSeleccionado(null)}>
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Calendario;

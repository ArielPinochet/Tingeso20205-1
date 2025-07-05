import React, { useState, useEffect, useMemo } from "react";
import { Calendar, momentLocalizer, Views } from "react-big-calendar";
import moment from "moment";
import "moment/locale/es";
import "react-big-calendar/lib/css/react-big-calendar.css";
import axios from "axios";
import { obtenerReservaPorId } from "../Services/ReservaService";

moment.locale("es");
const localizer = momentLocalizer(moment);

const CALENDARIO_URL = "http://localhost:8080/api/calendario/todos";

const Calendario = () => {
  const [eventos, setEventos] = useState([]);
  const [eventoSeleccionado, setEventoSeleccionado] = useState(null);
  const [detalleReserva, setDetalleReserva] = useState(null);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);
  const [currentDate, setCurrentDate] = useState(new Date());
  const [currentView, setCurrentView] = useState(Views.MONTH);
  const [errorCalendario, setErrorCalendario] = useState(false); // Nuevo estado

  useEffect(() => {
    axios.get(CALENDARIO_URL)
      .then((response) => {
        const data = Array.isArray(response.data) ? response.data : [];
        const eventosProcesados = data.map((item) => ({
          id: item.id,
          title: `Reserva ${item.reservaId} - ${item.clienteNombre}`,
          start: new Date(`${item.fecha}T${item.horaInicio}`),
          end: new Date(`${item.fecha}T${item.horaFin}`),
          allDay: false,
          resource: item,
        }));
        setEventos(eventosProcesados);
        setErrorCalendario(false);
      })
      .catch((error) => {
        // Mostrar error para cualquier tipo de fallo (no solo 500)
        setErrorCalendario(true);
        console.error("🚨 Error al obtener calendario:", error);
      });
  }, []);

  useEffect(() => {
    if (eventoSeleccionado && eventoSeleccionado.resource?.reservaId) {
      setCargandoDetalle(true);
      obtenerReservaPorId(eventoSeleccionado.resource.reservaId)
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

  const minTime = useMemo(() => {
    const d = new Date();
    d.setHours(6, 0, 0, 0);
    return d;
  }, []);
  const maxTime = useMemo(() => {
    const d = new Date();
    d.setHours(22, 0, 0, 0);
    return d;
  }, []);

  const customMonthDateHeader = ({ label }) => (
    <div style={{ fontWeight: 700, color: "#1976d2", marginBottom: 2 }}>{label}</div>
  );

  return (
    <div style={{
      maxWidth: 1100,
      margin: "0 auto",
      marginTop: "1.5rem",
      marginBottom: "2rem",
      background: "#fff",
      borderRadius: "18px",
      boxShadow: "0 2px 16px rgba(0,0,0,0.07)",
      padding: "1.5rem"
    }}>
      <h2 style={{
        fontWeight: 700,
        color: "#1976d2",
        letterSpacing: "1px",
        marginBottom: "0.5rem",
        fontSize: "1.4rem"
      }}>
        Calendario de Ocupación de la Pista
      </h2>
      <div className="alert alert-info py-2 mb-3" style={{
        fontSize: "1rem",
        background: "#e3f2fd",
        color: "#1976d2",
        border: "none",
        borderRadius: "8px"
      }}>
        Click en la reserva para más detalles
      </div>
      {errorCalendario ? (
        <div className="alert alert-danger text-center my-4" style={{ fontSize: "1.1rem" }}>
          Servicio de calendario temporalmente fuera de servicio, Intentelo de nuevo más tarde.
        </div>
      ) : (
        <Calendar
          localizer={localizer}
          events={eventos}
          defaultView={Views.MONTH}
          view={currentView}
          onView={view => setCurrentView(view)}
          views={["month", "week", "day"]}
          startAccessor="start"
          endAccessor="end"
          style={{ height: currentView === "month" ? 700 : 500, fontSize: "1rem", borderRadius: "12px" }}
          min={minTime}
          max={maxTime}
          onSelectEvent={event => setEventoSeleccionado(event)}
          popup
          eventPropGetter={event => ({
            style: {
              backgroundColor: "#1976d2",
              color: "#fff",
              borderRadius: "8px",
              border: "none",
              fontWeight: 500,
              fontSize: "1rem"
            }
          })}
          messages={{
            week: "Semana",
            day: "Día",
            month: "Mes",
            today: "Hoy",
            previous: "Anterior",
            next: "Siguiente",
            date: "Fecha",
            time: "Hora",
            event: "Evento",
            noEventsInRange: "No hay eventos en este rango.",
            allDay: "Todo el día"
          }}
          culture="es"
          date={currentDate}
          onNavigate={date => setCurrentDate(date)}
          dayLayoutAlgorithm="no-overlap"
          components={{
            month: {
              dateHeader: customMonthDateHeader
            }
          }}
          formats={{
            dayFormat: (date, culture, localizer) =>
              localizer.format(date, "ddd", culture),
            weekdayFormat: (date, culture, localizer) =>
              localizer.format(date, "dddd", culture),
            monthHeaderFormat: (date, culture, localizer) =>
              localizer.format(date, "MMMM yyyy", culture),
            dayHeaderFormat: (date, culture, localizer) =>
              localizer.format(date, "dddd DD/MM", culture),
            dayRangeHeaderFormat: ({ start, end }, culture, localizer) =>
              `${localizer.format(start, "DD MMM", culture)} – ${localizer.format(end, "DD MMM", culture)}`
          }}
        />
      )}

      {/* Modal de detalle de reserva */}
      {eventoSeleccionado && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 4000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "16px",
            boxShadow: "0 2px 24px rgba(25,118,210,0.18)", minWidth: "340px", maxWidth: "90vw"
          }}>
            <h4 style={{ color: "#1976d2", fontWeight: 600 }}>Detalle de Reserva</h4>
            {cargandoDetalle && (
              <div className="text-center my-3">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Cargando...</span>
                </div>
                <div>Cargando detalles...</div>
              </div>
            )}
            {detalleReserva && (
              <ul style={{ listStyle: "none", padding: 0, fontSize: "1.08rem" }}>
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
                <li>
                  <b>Hora Fin:</b>{" "}
                  {detalleReserva.horaInicio && detalleReserva.numeroVueltas
                    ? (() => {
                        const inicio = new Date(detalleReserva.horaInicio);
                        let minutos = 0;
                        if (detalleReserva.numeroVueltas === 10) minutos = 30;
                        else if (detalleReserva.numeroVueltas === 15) minutos = 35;
                        else if (detalleReserva.numeroVueltas === 20) minutos = 40;
                        else minutos = 0;
                        inicio.setMinutes(inicio.getMinutes() + minutos);
                        return inicio.toLocaleTimeString("es-CL", {
                          hour: "2-digit",
                          minute: "2-digit"
                        });
                      })()
                    : ""}
                </li>
                <li><b>Número de Vueltas:</b> {detalleReserva.numeroVueltas}</li>
                <li><b>Cantidad de Personas:</b> {detalleReserva.cantidadPersonas}</li>
                <li><b>Día Especial:</b> {detalleReserva.diaEspecial ? "Sí" : "No"}</li>
                <li><b>Carros:</b> {detalleReserva.carros && detalleReserva.carros.length > 0 ? detalleReserva.carros.join(", ") : "No asignados"}</li>
              </ul>
            )}
            {!cargandoDetalle && !detalleReserva && (
              <div className="text-danger">No se pudo cargar el detalle de la reserva, Intentelo de nuevo más tarde.</div>
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

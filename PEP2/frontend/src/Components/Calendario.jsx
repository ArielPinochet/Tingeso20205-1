import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es'; 
import axios from 'axios';

const CALENDARIO_URL = "http://localhost:8080/api/calendario/todos";

const Calendario = () => {
  const [eventos, setEventos] = useState([]);

  useEffect(() => {
    axios.get(CALENDARIO_URL)
      .then((response) => {
        const data = Array.isArray(response.data) ? response.data : [];
        const eventosProcesados = data.map((item) => {
          // Construir fecha de inicio y fin en formato ISO
          const start = `${item.fecha}T${item.horaInicio}`;
          const end = `${item.fecha}T${item.horaFin}`;
          return {
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

  return (
    <div>
      <h2>Calendario de Ocupación de la Pista</h2>
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
      />
    </div>
  );
};

export default Calendario;

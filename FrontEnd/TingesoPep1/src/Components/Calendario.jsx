import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react'; // Componente principal de FullCalendar
import timeGridPlugin from '@fullcalendar/timegrid'; // Plugin para vista en rejilla horaria
import interactionPlugin from '@fullcalendar/interaction'; // Para funciones de interacción
import esLocale from '@fullcalendar/core/locales/es';  // Importa la localización en español

const Calendario = () => {
  const [reservas, setReservas] = useState([]);

  useEffect(() => {
    // Aquí debes reemplazar este array de ejemplo por la llamada a tu API
    // que retorne las reservas reales de la pista.
    const data = [
      { id: 1, title: "Reserva 1", start: "2025-04-27T10:00:00", duration: 60 },
      { id: 2, title: "Reserva 2", start: "2025-04-27T12:00:00", duration: 90 },
      { id: 3, title: "Reserva 3", start: "2025-04-28T09:00:00", duration: 120 },
    ];

    // Convertir cada reserva en un evento aceptado por FullCalendar.
    const eventos = data.map((r) => {
      const startDate = new Date(r.start);
      // Sumar la duración (en minutos) en milisegundos.
      const endDate = new Date(startDate.getTime() + r.duration * 60000);
      return {
        id: r.id,
        title: r.title, // Puedes usar "Ocupada" u otro texto si lo prefieres.
        start: r.start,
        end: endDate.toISOString(),
      };
    });

    setReservas(eventos);
  }, []);

  return (
    <div>
      <h2>Rack Semanal de Ocupación de la Pista</h2>
      <FullCalendar
        plugins={[timeGridPlugin, interactionPlugin]}
        initialView="timeGridWeek"
        locale={esLocale} // Con esto se muestra en español.
        headerToolbar={{
          left: "prev,next hoy",
          center: "title",
          right: "timeGridWeek,timeGridDay"
        }}
        allDaySlot={false}
        slotMinTime="06:00:00"
        slotMaxTime="23:00:00"
        events={reservas}
        height="auto"
      />
    </div>
  );
};

export default Calendario;

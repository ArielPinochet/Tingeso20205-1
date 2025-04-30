import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import esLocale from '@fullcalendar/core/locales/es'; 
import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8090";

const RESERVA_URL = `${API_URL}/reservas`;


const Calendario = () => {
  const [reservas, setReservas] = useState([]);

  useEffect(() => {
    console.log("Iniciando solicitud al backend...");
    
    axios.get(RESERVA_URL)
      .then((response) => {
        console.log("Respuesta completa del backend:", response);
        console.log("Datos en response.data:", response.data);

        // Validar que la respuesta es un array de reservas
        const data = Array.isArray(response.data) ? response.data : response.data.reservas || [];
        console.log("Reservas procesadas:", data);

        if (!Array.isArray(data)) {
          console.error("Error: La respuesta del backend no es un array. Recibido:", data);
          return;
        }

        const eventos = data.map((r) => {
          console.log("Procesando reserva:", r); // Ver los datos reales
        
          const startDateParts = r.fechaReserva.split("-");
          const timeParts = r.horaInicio.split(":");
          
          const startDate = new Date(
            Number(startDateParts[0]),  // Año
            Number(startDateParts[1]) - 1,  // Mes (JS usa 0-indexado)
            Number(startDateParts[2]) - 1,  // Día
            Number(timeParts[0]),  // Hora
            Number(timeParts[1])   // Minutos
          );
        
          // Verificar si `startDate` es válido antes de seguir
          if (isNaN(startDate.getTime())) {
            console.error(`Hora de inicio inválida para reserva ID ${r.id}:`, r.horaInicio);
            return null;
          }
        
          // Calcular la hora de fin sumando la duración total en minutos
          const endDate = new Date(startDate.getTime() + r.duracionTotal * 60000);
        
          console.log(`Reserva ${r.id}: Fecha ${r.fechaReserva}, Inicio ${startDate.toISOString()}, Fin ${endDate.toISOString()}`);
        
          return {
            id: r.id || `Reserva-${Math.random()}`,
            title: `Reserva ${r.id}`,
            start: startDate.toISOString(),
            end: endDate.toISOString(),
          };
        }).filter(Boolean); // Filtra las reservas inválidas
        

        console.log("Eventos finales a mostrar:", eventos);
        setReservas(eventos);
      })
      .catch((error) => {
        console.error("Error al obtener reservas:", error);
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
          left: "prev,next hoy",
          center: "title",
          right: "timeGridWeek,timeGridDay"
        }}
        allDaySlot={false}
        slotMinTime="05:00:00"
        slotMaxTime="23:00:00"
        events={reservas} // 📌 Usa las reservas obtenidas del backend
        height="auto"
      />
    </div>
  );
};

export default Calendario;

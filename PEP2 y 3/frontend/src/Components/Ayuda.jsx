import React, { useEffect } from 'react';
import Balatro from "balatrobutton";



const Ayuda = () => {
  useEffect(() => {
    // Achica el efecto de fuego reduciendo power y score
    const balatroBtn = new Balatro({
      selector: '.balatro-btn',
      duration: 0,
      power: () => 200, // Menor valor = fuego más pequeño
      score: 0,
      colorStart: "#f50",
      colorMid: "#f50",
      colorEnd: "#f50",
    });
    // Opcional: puedes acceder a funciones extra aquí si lo necesitas
    //  );
    // balatroBtn.setScore(1000);
    // balatroBtn.stopAll();
  }, []);

  return (
    <div className="ayuda-container" style={styles.container}>
      <h1 style={styles.title}>Centro de Ayuda</h1>

      <details style={styles.section}>
        <summary style={styles.summary}>➕ Crear Cliente</summary>
        <ol style={styles.list}>
          <li>
            Ve a <strong>Clientes</strong> y haz clic en <strong>Agregar Cliente</strong>.
          </li>
          <li>
            Completa los campos requeridos:
            <ul>
              <li><strong>Nombre</strong> (sin espacios ni mayúsculas)</li>
              <li><strong>Email</strong> (elige dominio o escribe uno personalizado)</li>
            </ul>
          </li>
          <li>
            Haz clic en <strong>Guardar</strong>. Si el cliente se crea correctamente, verás un mensaje de éxito.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>Nombre ya existe: usa otro nombre.</li>
              <li>Email inválido: revisa el formato (ejemplo: usuario@dominio.com).</li>
              <li>Servicio fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <details style={styles.section}>
        <summary style={styles.summary}>📋 Arrendar un Kart / Registrar Reserva</summary>
        <ol style={styles.list}>
          <li>
            Ve a <strong>Reservas</strong> y haz clic en <strong>Nueva Reserva</strong>.
          </li>
          <li>
            Completa el formulario:
            <ul>
              <li><strong>Fecha de Reserva</strong> (no puede ser pasada)</li>
              <li><strong>Hora de Inicio</strong> (entre 06:00 y 21:59)</li>
              <li><strong>Número de Vueltas</strong></li>
              <li><strong>Cantidad de Personas</strong></li>
              <li><strong>¿Es cumpleaños?</strong> (elige Sí o No)</li>
              <li><strong>Cliente Responsable</strong> (puedes crear uno nuevo si no existe)</li>
              <li><strong>Selecciona Kart</strong> para cada persona (usa "Selección rápida" si no tienes preferencia)</li>
            </ul>
          </li>
          <li>
            Haz clic en <strong>Guardar</strong>. Verás un mensaje de éxito si la reserva se registra correctamente.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>No hay carros suficientes: reduce la cantidad de personas o cambia la hora.</li>
              <li>No puedes reservar una fecha/hora pasada.</li>
              <li>El horario permitido es de 06:00 a 21:59.</li>
              <li>Debes seleccionar un cliente válido.</li>
              <li>Servicio fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <details style={styles.section}>
        <summary style={styles.summary}>✏️ Editar Cliente o Reserva</summary>
        <ol style={styles.list}>
          <li>
            Ve a la lista de <strong>Clientes</strong> o <strong>Reservas</strong>.
          </li>
          <li>
            Haz clic en <strong>Editar</strong> junto al registro que deseas modificar.
          </li>
          <li>
            Cambia los datos necesarios y haz clic en <strong>Actualizar</strong>.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>No puedes editar reservas del mismo día (a menos que seas administrador).</li>
              <li>No puedes editar reservas ya pagadas (a menos que seas administrador).</li>
              <li>Servicio fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <details style={styles.section}>
        <summary style={styles.summary}>💳 Pagar una Reserva</summary>
        <ol style={styles.list}>
          <li>
            Ve a <strong>Reservas</strong> y busca la reserva pendiente de pago.
          </li>
          <li>
            Haz clic en <strong>Proceder al Pago</strong>.
          </li>
          <li>
            Ingresa los correos de los participantes (uno por persona).
          </li>
          <li>
            Revisa el detalle del pago y haz clic en <strong>Aceptar</strong> para generar el comprobante.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>Correo inválido: revisa el formato de los correos.</li>
              <li>Servicio fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <details style={styles.section}>
        <summary style={styles.summary}>📅 Ver Calendario</summary>
        <ol style={styles.list}>
          <li>
            Ve a <strong>Calendario</strong> en el menú principal.
          </li>
          <li>
            Cambia la vista a mes, semana o día según prefieras.
          </li>
          <li>
            Haz clic en una reserva para ver sus detalles.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>Servicio de calendario fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <details style={styles.section}>
        <summary style={styles.summary}>📊 Obtener Reporte</summary>
        <ol style={styles.list}>
          <li>
            Ve a <strong>Reportes</strong> en el menú.
          </li>
          <li>
            Selecciona el tipo de reporte (por vueltas o por personas).
          </li>
          <li>
            Elige el rango de meses.
          </li>
          <li>
            Haz clic en <strong>Descargar Reporte en Excel</strong>.
          </li>
          <li>
            <strong>Errores comunes:</strong>
            <ul>
              <li>No seleccionaste ambos meses: selecciona mes de inicio y fin.</li>
              <li>Servicio de reportes fuera de servicio: inténtalo más tarde.</li>
            </ul>
          </li>
        </ol>
      </details>

      <div style={{ margin: "12px 0", textAlign: "center" }}>
        <button className="balatro-btn" style={{ padding: "12px 32px", fontSize: "1.2rem", borderRadius: "8px", border: "none", background: "#222", color: "#fff", cursor: "pointer" }}>
          2000
        </button>
        <div style={{ fontSize: "0.95rem", marginTop: 4, color: "#555" }}>
          Haz clic para ver el efecto de BalatroButton.
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: 800,
    margin: '0 auto',
    padding: 20,
    fontFamily: 'Arial, sans-serif',
    lineHeight: 1.5
  },
  title: {
    textAlign: 'center',
    marginBottom: 24
  },
  section: {
    marginBottom: 16,
    border: '1px solid #ddd',
    borderRadius: 4,
    padding: '8px 12px',
    background: '#fafafa'
  },
  summary: {
    fontSize: 16,
    fontWeight: 'bold',
    cursor: 'pointer'
  },
  list: {
    marginTop: 8,
    paddingLeft: 20
  }
};

export default Ayuda;
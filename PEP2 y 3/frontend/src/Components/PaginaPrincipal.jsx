import React from "react";

const PaginaPrincipal = () => {
  const lat = -33.449838;
  const lng = -70.687203;
  const googleMapsUrl = `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Bienvenido a KartingRM</h1>
      <div className="row">
        <div className="col-md-7">
          <h3>Sobre Nosotros</h3>
          <p>
            En KartingRM somos líderes en la industria del karting en la Región Metropolitana, comprometidos con ofrecer una experiencia emocionante, segura y eficiente a todos nuestros clientes. 
            Frente al crecimiento de la demanda y las expectativas de nuestros usuarios, hemos desarrollado un sistema integral de reservas y gestión de recursos que nos permite optimizar cada aspecto de nuestra operación.
          </p>
          <p>
            Nuestro sistema facilita la reserva de horarios, la asignación de karts y el seguimiento del estado de cada vehículo, eliminando los retrasos y errores propios de los procesos manuales. Así, garantizamos confirmaciones rápidas, disponibilidad real y una atención de excelencia, superando las expectativas de quienes nos visitan.
          </p>
          <p>
            Contamos con 15 karts individuales modelo Sodikart RT8, todos identificados y sometidos a mantenimiento preventivo regular para asegurar su óptimo funcionamiento y la seguridad de nuestros clientes. Nuestro horario de atención es de lunes a viernes de 14:00 a 22:00 horas, y sábados, domingos y feriados de 10:00 a 22:00 horas.
          </p>
          <p>
            En KartingRM, tu diversión y seguridad son nuestra prioridad. ¡Te invitamos a vivir la mejor experiencia de karting con nosotros!
          </p>
          <h4>Contáctanos</h4>
          <ul>
            <li>Teléfono: +56 9 8792 6366</li>
            <li>Dirección: Departamento de Ingeniería Informática - DIINF USACH</li>
          </ul>
        </div>
        <div className="col-md-5">
          <h4>Ubicación</h4>
          <div style={{ width: "100%", height: "320px", borderRadius: "10px", overflow: "hidden", boxShadow: "0 2px 8px rgba(0,0,0,0.1)" }}>
            <iframe
              title="Ubicación KartingRM"
              width="100%"
              height="320"
              frameBorder="0"
              style={{ border: 0, filter: "grayscale(30%) contrast(1.2) brightness(1.1)" }}
              src={`https://www.openstreetmap.org/export/embed.html?bbox=${lng-0.002}%2C${lat-0.001}%2C${lng+0.002}%2C${lat+0.001}&layer=cyclemap&marker=${lat},${lng}`}
              allowFullScreen=""
              aria-hidden="false"
              tabIndex="0"
            ></iframe>
          </div>
          <div className="mt-2">
            <span style={{ fontWeight: "bold", color: "#007bff" }}>📍KartingRM</span>
            <br />
            <a
              href={`https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=18/${lat}/${lng}`}
              target="_blank"
              rel="noopener noreferrer"
              className="me-3"
            >
              Ver mapa más grande
            </a>
            <a
              href={googleMapsUrl}
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: "#4285F4", fontWeight: "bold", marginLeft: "10px" }}
            >
              Cómo llegar / Buscar en Google Maps
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaginaPrincipal;
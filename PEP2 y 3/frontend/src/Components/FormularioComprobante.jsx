import React, { useState, useEffect, useMemo, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";
import axios from "axios";
import { crearComprobante } from "../Services/ComprobanteService";
import { obtenerClientes } from "../Services/ClientService";

const EMAIL_DOMAINS = [
  "@gmail.com",
  "@outlook.com",
  "@hotmail.com",
  "@yahoo.com",
  "@icloud.com",
  "@usach.cl",
  "Otro"
];

const FormularioComprobante = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // Estados para los datos
  const [reserva, setReserva] = useState(null);
  const [tarifa, setTarifa] = useState(null);
  const [descuento, setDescuento] = useState(null);

  // Estado para los correos y errores
  const [correosClientes, setCorreosClientes] = useState([]);
  const [emailErrors, setEmailErrors] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [emailUserList, setEmailUserList] = useState([]); // [{user, domain, customDomain}]
  const [emailDomainList, setEmailDomainList] = useState([]); // ["@gmail.com", ...]
  const [showSuggestions, setShowSuggestions] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [activeInput, setActiveInput] = useState(null);

  // Estados para el guardado
  const [guardando, setGuardando] = useState(false);
  const [comprobanteGuardado, setComprobanteGuardado] = useState(false);

  // Regex para validar correo
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Cargar datos al montar
  useEffect(() => {
    // 1. Obtener reserva
    axios.get(`http://localhost:8080/app/reservas/${id}`)
      .then(res => {
        setReserva(res.data);
        setCorreosClientes(Array.from({ length: res.data.cantidadPersonas }, () => ""));
        setEmailErrors(Array.from({ length: res.data.cantidadPersonas }, () => ""));
        setEmailUserList(Array.from({ length: res.data.cantidadPersonas }, () => ""));
        setEmailDomainList(Array.from({ length: res.data.cantidadPersonas }, () => EMAIL_DOMAINS[0]));
        setShowSuggestions(Array.from({ length: res.data.cantidadPersonas }, () => false));
      });

    // 2. Obtener tarifa
    axios.get(`http://localhost:8080/api/tarifas/${id}`)
      .then(res => setTarifa(res.data));

    // 3. Obtener descuento
    axios.get(`http://localhost:8080/api/descuentos/obtener/${id}`)
      .then(res => setDescuento(res.data));

    // 4. Obtener clientes para sugerencias de correo
    obtenerClientes().then(res => {
      // Evitar duplicados
      const correosUnicos = Array.from(new Set(res.data.map(c => c.email).filter(Boolean)));
      setClientes(correosUnicos);
    });
  }, [id]);

  // Calcular totales
  const precioSinDescuento = tarifa?.precio || 0;
  const descuentoPorcentaje = descuento?.descuentoTotal || 0;
  const precioConDescuento = precioSinDescuento - (precioSinDescuento * descuentoPorcentaje / 100);
  const precioSinIva = +(precioConDescuento / 1.19).toFixed(2);
  const precioConIva = +(precioConDescuento).toFixed(2);

  // --- Email helpers para cada input ---
  const handleEmailUserChange = (index, value) => {
    // Solo la parte antes de @, sin espacios
    const user = value.replace(/\s+/g, "");
    setEmailUserList(prev => {
      const arr = [...prev];
      arr[index] = user;
      return arr;
    });
    // Actualiza el correo completo
    updateCorreoCompleto(index, user, emailDomainList[index], "");
    // Sugerencias
    if (user.length > 0) {
      const sugerencias = clientes.filter(correo => correo.toLowerCase().startsWith(user.toLowerCase()));
      setSuggestions(sugs => {
        const arr = [...sugs];
        arr[index] = sugerencias;
        return arr;
      });
      setShowSuggestions(prev => {
        const arr = [...prev];
        arr[index] = true;
        return arr;
      });
    } else {
      setShowSuggestions(prev => {
        const arr = [...prev];
        arr[index] = false;
        return arr;
      });
    }
  };

  const handleEmailDomainChange = (index, value) => {
    setEmailDomainList(prev => {
      const arr = [...prev];
      arr[index] = value;
      return arr;
    });
    if (value !== "Otro") {
      updateCorreoCompleto(index, emailUserList[index], value, "");
    } else {
      updateCorreoCompleto(index, emailUserList[index], value, "");
    }
  };

  const handleCustomDomainChange = (index, value) => {
    // Solo para "Otro"
    updateCorreoCompleto(index, emailUserList[index], "Otro", value.replace(/\s+/g, ""));
  };

  const updateCorreoCompleto = (index, user, domain, customDomain) => {
    let email = user;
    if (user) {
      if (domain === "Otro" && customDomain) {
        email += "@" + customDomain;
      } else if (domain !== "Otro") {
        email += domain;
      }
    }
    setCorreosClientes(prev => {
      const arr = [...prev];
      arr[index] = email;
      return arr;
    });
    // Validación
    let errorMessage = "";
    if (email && !emailRegex.test(email)) {
      errorMessage = "El correo es inválido. Ingrese un correo con el formato x@x.xx";
    }
    setEmailErrors(prev => {
      const arr = [...prev];
      arr[index] = errorMessage;
      return arr;
    });
  };

  // Cuando selecciona una sugerencia
  const handleSuggestionClick = (index, correo) => {
    setCorreosClientes(prev => {
      const arr = [...prev];
      arr[index] = correo;
      return arr;
    });
    setEmailUserList(prev => {
      const arr = [...prev];
      arr[index] = correo.split("@")[0];
      return arr;
    });
    setEmailDomainList(prev => {
      const arr = [...prev];
      const domain = "@" + correo.split("@")[1];
      arr[index] = EMAIL_DOMAINS.includes(domain) ? domain : "Otro";
      return arr;
    });
    setShowSuggestions(prev => {
      const arr = [...prev];
      arr[index] = false;
      return arr;
    });
  };

  // Mostrar sugerencias al enfocar
  const handleEmailFocus = (index) => {
    if (emailUserList[index] && clientes.length > 0) {
      const sugerencias = clientes.filter(correo => correo.toLowerCase().startsWith(emailUserList[index].toLowerCase()));
      setSuggestions(sugs => {
        const arr = [...sugs];
        arr[index] = sugerencias;
        return arr;
      });
      setShowSuggestions(prev => {
        const arr = [...prev];
        arr[index] = true;
        return arr;
      });
    }
    setActiveInput(index);
  };

  // Ocultar sugerencias al salir del input (con pequeño delay para permitir click)
  const handleEmailBlur = (index) => {
    setTimeout(() => {
      setShowSuggestions(prev => {
        const arr = [...prev];
        arr[index] = false;
        return arr;
      });
    }, 150);
  };

  // Generar PDF
  const generarPDFBlob = () => {
    const doc = new jsPDF();
    doc.setFont("helvetica", "bold");
    doc.setFontSize(16);
    doc.text("Comprobante de Pago", 70, 20);
    doc.setFont("helvetica", "normal");
    doc.setFontSize(12);
    doc.rect(10, 30, 190, 110);
    doc.text(`ID Reserva: ${reserva.idReserva}`, 20, 40);
    doc.text(`Fecha de Emisión: ${new Date().toISOString().split("T")[0]}`, 20, 50);
    doc.text(`Fecha de Reserva: ${reserva.fechaReserva.split("T")[0]}`, 20, 60);
    doc.text(`Hora de Inicio: ${reserva.horaInicio.split("T")[1]?.substring(0,5) || ""}`, 20, 70);
    doc.text(`Cantidad de Personas: ${reserva.cantidadPersonas}`, 20, 80);
    doc.text(`Número de Vueltas: ${reserva.numeroVueltas}`, 20, 90);
    doc.text(`Duración Total: ${tarifa?.duracionTotal || reserva.duracionTotal} min`, 20, 100);
    doc.text(`Cliente Responsable: ${reserva.nombreCliente}`, 20, 110);
    doc.text(`Precio sin descuento: $${precioSinDescuento.toFixed(2)}`, 20, 120);
    doc.text(`Descuento total: -$${descuentoTotal.toFixed(2)}`, 20, 130);
    doc.text(`Precio final (sin IVA): $${precioSinIva.toFixed(2)}`, 20, 140);
    doc.text(`Precio final (con IVA): $${precioConIva.toFixed(2)}`, 20, 150);

    // Correos de participantes
    let posY = 160;
    doc.rect(10, posY, 190, 40);
    doc.text("Correos de Participantes:", 20, posY + 10);
    correosClientes.forEach((correo, index) => {
      doc.text(`${index + 1}. ${correo}`, 20, posY + 20 + index * 10);
    });

    return doc.output("blob");
  };

  // Enviar comprobante
  const handleSubmit = async (e) => {
    e.preventDefault();

    setGuardando(true); // Mostrar modal de guardando

    // Construir los parámetros
    const params = new URLSearchParams({
      idReserva: reserva.idReserva,
      fechaEmision: new Date().toISOString().split("T")[0],
      totalConIva: precioConIva.toString(),
      fechaReserva: reserva.fechaReserva.split("T")[0],
      horaInicio: reserva.horaInicio.split("T")[1]?.substring(0,5) || "",
      cantidadPersonas: reserva.cantidadPersonas,
      numeroVueltas: reserva.numeroVueltas,
      duracionTotal: tarifa?.duracionTotal || reserva.duracionTotal,
      nombreCliente: reserva.nombreCliente,
      precioFinalSinIVA: precioSinIva.toString(),
      correosClientes: correosClientes.join(",") // <--- aquí el cambio
    });

    const url = `http://localhost:8080/app/comprobantes?${params.toString()}`;
    console.log("URL comprobante:", url);

    try {
      const response = await fetch(url, { method: "POST" });
      setGuardando(false);
      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData);
      }
      setComprobanteGuardado(true); // Mostrar modal de éxito
    } catch (error) {
      setGuardando(false);
      console.error("Error al enviar comprobante:", error);
      alert("Error al enviar comprobante. Revisa la consola.");
    }
  };

  if (!reserva || !tarifa || !descuento) {
    return <div>Cargando datos de la reserva...</div>;
  }

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
            <h5>Generando comprobante...</h5>
            <div className="spinner-border text-primary mt-3" role="status">
              <span className="visually-hidden">Guardando...</span>
            </div>
          </div>
        </div>
      )}

      {/* Modal de comprobante generado */}
      {comprobanteGuardado && (
        <div style={{
          position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh",
          background: "rgba(0,0,0,0.3)", zIndex: 3000,
          display: "flex", alignItems: "center", justifyContent: "center"
        }}>
          <div style={{
            background: "#fff", padding: "2rem", borderRadius: "10px",
            boxShadow: "0 2px 16px rgba(0,0,0,0.2)", minWidth: "320px", textAlign: "center"
          }}>
            <h5>¡Comprobante generado y enviado!</h5>
            <button className="btn btn-success mt-3" onClick={() => {
              setComprobanteGuardado(false);
              navigate("/comprobantes");
            }}>
              OK
            </button>
          </div>
        </div>
      )}

      <h2>Comprobante de Pago</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label>ID Reserva</label>
          <input type="text" className="form-control" value={reserva.idReserva} readOnly />
        </div>
        <div className="mb-3">
          <label>Fecha de Emisión</label>
          <input type="date" className="form-control" value={new Date().toISOString().split("T")[0]} readOnly />
        </div>
        <div className="mb-3">
          <label>Fecha de Reserva</label>
          <input type="date" className="form-control" value={reserva.fechaReserva.split("T")[0]} readOnly />
        </div>
        <div className="mb-3">
          <label>Hora de Inicio</label>
          <input type="text" className="form-control" value={reserva.horaInicio.split("T")[1]?.substring(0,5) || ""} readOnly />
        </div>
        <div className="mb-3">
          <label>Cantidad de Personas</label>
          <input type="number" className="form-control" value={reserva.cantidadPersonas} readOnly />
        </div>
        <div className="mb-3">
          <label>Número de Vueltas</label>
          <input type="text" className="form-control" value={reserva.numeroVueltas} readOnly />
        </div>
        <div className="mb-3">
          <label>Duración Total (en minutos)</label>
          <input type="number" className="form-control" value={tarifa?.duracionTotal || reserva.duracionTotal} readOnly />
        </div>
        <div className="mb-3">
          <label>Cliente Responsable</label>
          <input type="text" className="form-control" value={reserva.nombreCliente} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio sin descuento</label>
          <input type="number" className="form-control" value={precioSinDescuento.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label> % de Descuento total</label>
          <input type="number" className="form-control" value={descuentoPorcentaje.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio final (sin IVA)</label>
          <input type="number" className="form-control" value={precioSinIva.toFixed(2)} readOnly />
        </div>
        <div className="mb-3">
          <label>Precio final (con IVA)</label>
          <input type="number" className="form-control" value={precioConIva.toFixed(2)} readOnly />
        </div>

        <h4>Correos de Participantes</h4>
        {correosClientes.map((correo, index) => (
          <div className="mb-3" key={index} style={{ position: "relative" }}>
            <label>Correo Participante {index + 1}</label>
            <div className="input-group">
              <input
                type="text"
                className={`form-control ${emailErrors[index] ? "is-invalid" : ""}`}
                placeholder="usuario"
                value={emailUserList[index] || ""}
                onChange={e => handleEmailUserChange(index, e.target.value)}
                onFocus={() => handleEmailFocus(index)}
                onBlur={() => handleEmailBlur(index)}
                autoComplete="off"
              />
              <select
                className="form-select"
                value={emailDomainList[index] || EMAIL_DOMAINS[0]}
                onChange={e => handleEmailDomainChange(index, e.target.value)}
              >
                {EMAIL_DOMAINS.map((domain) => (
                  <option key={domain} value={domain}>{domain === "Otro" ? "Otro..." : domain}</option>
                ))}
              </select>
              {emailDomainList[index] === "Otro" && (
                <input
                  type="text"
                  className="form-control"
                  placeholder="dominio.com"
                  value={
                    correo.includes("@") && !EMAIL_DOMAINS.includes("@" + correo.split("@")[1])
                      ? correo.split("@")[1]
                      : ""
                  }
                  onChange={e => handleCustomDomainChange(index, e.target.value)}
                />
              )}
            </div>
            {/* Sugerencias tipo Google */}
            {showSuggestions[index] && suggestions[index] && suggestions[index].length > 0 && (
              <ul
                style={{
                  position: "absolute",
                  top: "100%",
                  left: 0,
                  right: 0,
                  zIndex: 20,
                  background: "#fff",
                  border: "1px solid #ccc",
                  borderRadius: "0 0 8px 8px",
                  maxHeight: "180px",
                  overflowY: "auto",
                  margin: 0,
                  padding: "0.25rem 0",
                  listStyle: "none"
                }}
              >
                {suggestions[index].map((sug, i) => (
                  <li
                    key={sug}
                    style={{
                      padding: "0.5rem 1rem",
                      cursor: "pointer",
                      background: (correosClientes[index] === sug) ? "#f0f0f0" : "#fff"
                    }}
                    onMouseDown={() => handleSuggestionClick(index, sug)}
                  >
                    {sug}
                  </li>
                ))}
              </ul>
            )}
            {emailErrors[index] && (
              <div className="invalid-feedback">{emailErrors[index]}</div>
            )}
          </div>
        ))}
        <button type="submit" className="btn btn-success">
          Aceptar
        </button>
        <button
          type="button"
          className="btn btn-secondary ms-2"
          onClick={() => navigate("/comprobantes")}
        >
          Cancelar
        </button>
      </form>
    </div>
  );
};

export default FormularioComprobante;

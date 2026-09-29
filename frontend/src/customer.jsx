import React, {useState} from 'react';
import {createRoot} from 'react-dom/client';
import {UserPlus, Building2, MapPin, ShieldCheck} from 'lucide-react';
import './customer.css';
import PhotoUploadDialog from './PhotoUploadDialog';

const API = 'http://localhost:8080/api';
const empty = {nombreCompleto:'',contactoAlternativo:'',edad:'',fechaNacimiento:'',telefonoPersonal:'',telefonoTrabajo:'',email:'',emailTrabajo:'',fotografia:'',workshopId:'1',calle:'',numero:'',colonia:'',municipio:'',estado:'',codigoPostal:''};

/** Formulario React del caso de uso CustomerFacade.registerCustomer. La API conserva la autorización. */
function CustomerForm() {
  const [data, setData] = useState(empty), [notice, setNotice] = useState(''), [sending, setSending] = useState(false);
  const set = event => setData({...data, [event.target.name]: event.target.value});
  const submit = async event => {
    event.preventDefault(); setSending(true); setNotice('');
    const payload = {...data, edad:Number(data.edad), workshopId:Number(data.workshopId), address:{calle:data.calle,numero:data.numero,colonia:data.colonia,municipio:data.municipio,estado:data.estado,codigoPostal:data.codigoPostal}};
    try {
      const response = await fetch(`${API}/auth/customers`, {method:'POST', headers:{'Content-Type':'application/json','Authorization':`Bearer ${localStorage.getItem('token') || ''}`}, body:JSON.stringify(payload)});
      const body = await response.json(); if (!response.ok) throw Error(body.detail || body.message || 'No fue posible registrar el cliente.');
      setNotice(`✓ ${body.message} Folio: ${body.customerId}`); setData(empty);
    } catch (error) { setNotice(error.message === 'Failed to fetch' ? 'No se pudo conectar al API.' : error.message); }
    finally { setSending(false); }
  };
  return <main className="customer-page"><header><a href="/" className="customer-brand"><Building2/> Motor<span>Flow</span></a><span><ShieldCheck/> Superadmin, Administrador y Secretaria</span></header><section className="customer-card"><div className="customer-title"><div><UserPlus/><p>CLIENTES</p></div><h1>Registrar cliente</h1><p>Captura datos completos y asócialo a una sucursal.</p></div><form onSubmit={submit}><fieldset><legend>Datos personales</legend><Grid><Field label="Nombre completo" name="nombreCompleto" value={data.nombreCompleto} onChange={set}/><Field label="Contacto alternativo" name="contactoAlternativo" value={data.contactoAlternativo} onChange={set}/><Field label="Fecha de nacimiento" name="fechaNacimiento" type="date" value={data.fechaNacimiento} onChange={set}/><Field label="Edad" name="edad" type="number" min="0" max="130" value={data.edad} onChange={set}/><Field label="Teléfono personal" name="telefonoPersonal" inputMode="numeric" pattern="[0-9]{10}" hint="10 dígitos" value={data.telefonoPersonal} onChange={set}/><Field label="Teléfono de trabajo" name="telefonoTrabajo" inputMode="numeric" pattern="[0-9]{10}" required={false} value={data.telefonoTrabajo} onChange={set}/><Field label="Correo personal" name="email" type="email" value={data.email} onChange={set}/><Field label="Correo de trabajo" name="emailTrabajo" type="email" required={false} value={data.emailTrabajo} onChange={set}/><PhotoUploadDialog onPhotoChange={fotografia => setData({...data, fotografia})}/><Field label="Sucursal / taller" name="workshopId" type="number" min="1" value={data.workshopId} onChange={set}/></Grid></fieldset><fieldset><legend><MapPin/> Dirección</legend><Grid><Field label="Calle" name="calle" value={data.calle} onChange={set}/><Field label="Número" name="numero" value={data.numero} onChange={set}/><Field label="Colonia" name="colonia" value={data.colonia} onChange={set}/><Field label="Municipio" name="municipio" value={data.municipio} onChange={set}/><Field label="Estado" name="estado" value={data.estado} onChange={set}/><Field label="Código postal" name="codigoPostal" pattern="[0-9]{5}" hint="5 dígitos" value={data.codigoPostal} onChange={set}/></Grid></fieldset><button className="customer-submit" disabled={sending}>{sending ? 'Registrando…' : 'Registrar cliente'}</button>{notice && <p className="customer-notice">{notice}</p>}</form></section></main>;
}
function Grid({children}) { return <div className="form-grid">{children}</div>; }
function Field({label,hint,required=true,...props}) { return <label>{label}{hint && <small>{hint}</small>}<input required={required} {...props}/></label>; }
createRoot(document.getElementById('root')).render(<CustomerForm/>);

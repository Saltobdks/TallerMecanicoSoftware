import React, {useEffect, useState} from 'react';
import {createRoot} from 'react-dom/client';
import {UserPlus, Building2, MapPin, ShieldCheck} from 'lucide-react';
import './customer.css';
import PhotoUploadDialog from './PhotoUploadDialog';

const API = 'http://localhost:8080/api';
const empty = {nombreCompleto:'',contactoAlternativo:'',edad:'',fechaNacimiento:'',telefonoPersonal:'',telefonoTrabajo:'',email:'',emailTrabajo:'',fotografia:'',workshopId:'',calle:'',numero:'',colonia:'',municipio:'',estado:'',codigoPostal:''};
async function readBody(response) { const text=await response.text(); try { return text ? JSON.parse(text) : {}; } catch { return {message:text}; } }

/** Formulario React del caso de uso CustomerFacade.registerCustomer. La API conserva la autorización. */
function CustomerForm() {
  const [data, setData] = useState(empty), [notice, setNotice] = useState(''), [sending, setSending] = useState(false), [workshops, setWorkshops] = useState([]);
  const set = event => setData({...data, [event.target.name]: event.target.value});
  useEffect(() => { fetch(`${API}/workshops`, {headers:{Authorization:`Bearer ${localStorage.getItem('token') || ''}`}}).then(r=>r.ok?r.json():[]).then(setWorkshops).catch(()=>setWorkshops([])); }, []);
  const submit = async event => {
    event.preventDefault(); setSending(true); setNotice('');
    const payload = {...data, edad:Number(data.edad), workshopId:Number(data.workshopId), address:{calle:data.calle,numero:data.numero,colonia:data.colonia,municipio:data.municipio,estado:data.estado,codigoPostal:data.codigoPostal}};
    try {
      const response = await fetch(`${API}/auth/customers`, {method:'POST', headers:{'Content-Type':'application/json','Authorization':`Bearer ${localStorage.getItem('token') || ''}`}, body:JSON.stringify(payload)});
      const body = await readBody(response); if (!response.ok) throw Error(response.status===401||response.status===403 ? 'Tu sesión no tiene autorización. Inicia sesión nuevamente.' : body.detail || body.message || 'No fue posible registrar el cliente.');
      setNotice(`✓ ${body.message} Folio: ${body.customerId}`); setData(empty);
    } catch (error) { setNotice(error.message === 'Failed to fetch' ? 'No se pudo conectar al API.' : error.message); }
    finally { setSending(false); }
  };
  return <main className="customer-page"><header><a href="/" className="customer-brand"><Building2/> Motor<span>Flow</span></a><span><ShieldCheck/> Superadmin, Administrador y Secretaria</span></header><section className="customer-card"><div className="customer-title"><div><UserPlus/><p>CLIENTES</p></div><h1>Registrar cliente</h1><p>Captura datos completos y asócialo a una sucursal.</p></div><form onSubmit={submit}><fieldset><legend>Datos personales</legend><Grid><Field label="Nombre completo" name="nombreCompleto" value={data.nombreCompleto} onChange={set}/><Field label="Contacto alternativo" name="contactoAlternativo" value={data.contactoAlternativo} onChange={set}/><Field label="Fecha de nacimiento" name="fechaNacimiento" type="date" value={data.fechaNacimiento} onChange={set}/><Field label="Edad" name="edad" type="number" min="0" max="130" value={data.edad} onChange={set}/><Field label="Teléfono personal" name="telefonoPersonal" inputMode="numeric" pattern="[0-9]{10}" hint="10 dígitos" value={data.telefonoPersonal} onChange={set}/><Field label="Teléfono de trabajo" name="telefonoTrabajo" inputMode="numeric" pattern="[0-9]{10}" required={false} value={data.telefonoTrabajo} onChange={set}/><Field label="Correo personal" name="email" type="email" value={data.email} onChange={set}/><Field label="Correo de trabajo" name="emailTrabajo" type="email" required={false} value={data.emailTrabajo} onChange={set}/><PhotoUploadDialog onPhotoChange={fotografia => setData({...data, fotografia})}/><label>Sucursal / taller<select required name="workshopId" value={data.workshopId} onChange={set}><option value="">Selecciona un taller</option>{workshops.map(w=><option key={w.id} value={w.id}>{w.nombre}</option>)}</select></label></Grid></fieldset><fieldset><legend><MapPin/> Dirección</legend><AddressFields data={data} setData={setData}/></fieldset><button className="customer-submit" disabled={sending}>{sending ? 'Registrando…' : 'Registrar cliente'}</button>{notice && <p className="customer-notice">{notice}</p>}</form></section></main>;
}
function Grid({children}) { return <div className="form-grid">{children}</div>; }
function Field({label,hint,required=true,...props}) { return <label>{label}{hint && <small>{hint}</small>}<input required={required} {...props}/></label>; }
export function AddressFields({data,setData}) {
  const [colonias,setColonias]=useState([]), [message,setMessage]=useState(''), [manual,setManual]=useState(false);
  const set=e=>setData({...data,[e.target.name]:e.target.value});
  const lookup=async e=>{ const cp=e.target.value.replace(/\D/g,'').slice(0,5); setData({...data,codigoPostal:cp}); if(cp.length!==5)return; setMessage('Buscando código postal…'); try { const r=await fetch(`${API}/addresses/postal-codes/${cp}`,{headers:{Authorization:`Bearer ${localStorage.getItem('token')||''}`}}); const a=await readBody(r); if(!r.ok)throw Error(r.status===401||r.status===403?'Tu sesión no tiene autorización. Inicia sesión nuevamente.':a.detail||a.message||'CP no encontrado'); setColonias(a.colonias); setManual(false); setData(current=>({...current,codigoPostal:cp,estado:a.estado,municipio:a.municipio,colonia:a.colonias.length===1?a.colonias[0]:''})); setMessage('Selecciona la colonia.'); } catch(err){setColonias([]);setManual(true);setMessage(`${err.message}. Puedes capturar la dirección manualmente.`);} };
  return <Grid><Field label="Código postal" name="codigoPostal" inputMode="numeric" pattern="[0-9]{5}" hint="5 dígitos; autocompleta Estado y Municipio" value={data.codigoPostal} onChange={lookup}/><Field label="País" value="México" readOnly/><Field label="Estado" name="estado" value={data.estado} onChange={set}/><Field label="Municipio / Localidad" name="municipio" value={data.municipio} onChange={set}/><label>Colonia<select required name="colonia" value={data.colonia} onChange={e=>{if(e.target.value==='__manual__'){setManual(true);setData({...data,colonia:''});}else set(e);}}><option value="">Selecciona una colonia</option>{colonias.map(c=><option key={c} value={c}>{c}</option>)}<option value="__manual__">Capturar colonia manualmente</option></select></label>{manual&&<Field label="Colonia manual" name="colonia" value={data.colonia} onChange={set}/>}<Field label="Calle" name="calle" value={data.calle} onChange={set}/><Field label="Número" name="numero" value={data.numero} onChange={set}/><p className="address-help">{message || 'También puedes escribir Estado, Municipio y Colonia manualmente.'}</p></Grid>;
}
createRoot(document.getElementById('root')).render(<CustomerForm/>);

import {useState} from 'react';
import {ImagePlus, Upload, X} from 'lucide-react';

const MAX_FILE_SIZE = 15 * 1024 * 1024;
const ACCEPTED_TYPES = ['image/png', 'image/jpeg'];

/** Diálogo de carga local: acepta PNG/JPG hasta 15 MB y entrega una cadena Base64 al formulario. */
export default function PhotoUploadDialog({onPhotoChange}) {
  const [open, setOpen] = useState(false);
  const [fileName, setFileName] = useState('Sin fotografía seleccionada');
  const [error, setError] = useState('');

  const selectPhoto = event => {
    const file = event.target.files?.[0];
    setError('');
    if (!file) return;
    if (!ACCEPTED_TYPES.includes(file.type)) { setError('Selecciona una imagen PNG o JPG.'); return; }
    if (file.size > MAX_FILE_SIZE) { setError('La imagen no puede superar 15 MB.'); return; }
    const reader = new FileReader();
    reader.onload = () => { onPhotoChange(reader.result); setFileName(file.name); setOpen(false); };
    reader.onerror = () => setError('No fue posible leer la imagen seleccionada.');
    reader.readAsDataURL(file);
  };

  return <div className="photo-field"><span>Fotografía <small>PNG o JPG · máximo 15 MB</small></span><button type="button" className="photo-trigger" onClick={() => setOpen(true)}><ImagePlus/> {fileName}</button>{open && <div className="photo-backdrop" role="presentation"><section className="photo-dialog" role="dialog" aria-modal="true" aria-labelledby="photo-title"><button type="button" className="photo-close" aria-label="Cerrar" onClick={() => setOpen(false)}><X/></button><ImagePlus className="photo-icon"/><h2 id="photo-title">Subir fotografía</h2><p>Selecciona un archivo PNG o JPG de hasta 15 MB.</p><label className="photo-picker"><Upload/> Elegir imagen<input type="file" accept="image/png,image/jpeg" onChange={selectPhoto}/></label>{error && <p className="photo-error" role="alert">{error}</p>}<button type="button" className="photo-cancel" onClick={() => setOpen(false)}>Cancelar</button></section></div>}</div>;
}

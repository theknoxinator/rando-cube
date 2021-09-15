export function AddMultiField(props) {
  const fields = props.fields.map((field) => {
    let options;
    if (field.type === 'SELECT') {
      options = field.options.map((option) => {
        return (
          <option key={option.value} value={option.value}>{option.text}</option>
        );
      });
    }

    return (
      <div key={field.key} className="col my-1">
        {field.type === 'TEXT' &&
          <input type="text" value={props.values[field.key]} placeholder={field.key} onChange={(e) => props.onChange(field.key, e)} />
        }
        {field.type === 'SELECT' &&
          <select className="form-select" value={props.values[field.key]} aria-label={field.optionsLabel}
                  disabled={field.disabled} onChange={(e) => props.onChange(field.key, e)}>
            {options}
          </select>
        }
      </div>
    );
  });

  return (
    <div className="row my-2">
      {fields}
      <div className="col-4">
        <button className="btn btn-primary mx-1" onClick={props.onSubmit}><i className="bi-plus-square"></i></button>
        <button className="btn btn-outline-danger mx-1" onClick={props.onCancel}><i className="bi-x-square"></i></button>
      </div>
    </div>
  );
}
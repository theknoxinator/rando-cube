export function AddButton(props) {
  return (
    <div className="row my-2">
      <div className="col">
        <button className="btn btn-primary" onClick={props.onClick}><i className="bi-plus-square"></i></button>
      </div>
    </div>
  );
}
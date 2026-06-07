import "./ui.css";

type Variant = "primary" | "ghost" | "danger" | "subtle";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  small?: boolean;
}

export default function Button({
  variant = "primary",
  small = false,
  className = "",
  children,
  ...rest
}: ButtonProps) {
  return (
    <button
      className={`btn btn--${variant}${small ? " btn--sm" : ""} ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
}

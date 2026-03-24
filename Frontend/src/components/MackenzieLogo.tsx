export function MackenzieLogo({ className = 'h-10 w-auto max-w-[4rem]' }: { className?: string }) {
  return (
    <img
      src="/mackenzie-logo.jpg"
      alt="Logo Mackenzie"
      className={`object-contain ${className}`}
      onError={(event) => {
        event.currentTarget.onerror = null;
        event.currentTarget.src = '/mackenzie-logo.svg';
      }}
    />
  );
}

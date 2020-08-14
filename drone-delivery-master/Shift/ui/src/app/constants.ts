declare global {
  interface Window {
      _SERVER_ENDPOINT: string
  }
}

window._SERVER_ENDPOINT = window._SERVER_ENDPOINT || "";

export {};

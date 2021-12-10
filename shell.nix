with import <nixpkgs> {};

stdenv.mkDerivation {
    name = "tocen-live";
    buildInputs = [
      ansible
      websocat
    ];
    shellHook = ''
    
    '';
}

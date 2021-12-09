with import <nixpkgs> {};

stdenv.mkDerivation {
    name = "tocen-live";
    buildInputs = [
        ansible
    ];
    shellHook = ''
    
    '';
}

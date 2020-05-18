type Person = Readonly<{
  navn: string;
  alder: number;
  personnummer: string;
  erKvinne: boolean;
  diskresjonskode?: string;
  dodsdato?: string;
}>

export default Person;

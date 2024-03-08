import {SecuredResource} from "./secured-resource";
import {Renderer} from "./renderer";

export interface Template extends SecuredResource {
  id: string;
  name: string;
  svg: string | undefined;
  img: string | undefined;
  xml: string | undefined;
  markers: string[];
  folder: string;
  usages: Renderer[]

}

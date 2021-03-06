import {Item} from './Item';

export class Recipe {
  name: string;
  pictureItem: string;
  interactionGrp: string;
  primaryItem: Item;
  returnedItems: Item[];
  tools: string[];
  components: Item[];
  requiresOcean: boolean;
  requiresFire: boolean;
  requiresConcreteMixer: boolean;
  category: string;
}

import type PostDTO from "~/dtos/PostDTO";
import type { UserDTO } from "~/dtos/UserDTO";


const API_POSTS_URL = "/api/v1/posts/";
const PAGE_SIZE = 25;

export async function getFeedForUser(page: number, user: UserDTO | null): Promise<{ hasMore: boolean; items: PostDTO[] }> {
    try {
        const response = await fetch(
            `${API_POSTS_URL}?page=${page}&size=${PAGE_SIZE}&userId=${user ? user.id : ""}`
        );
        if (!response.ok) throw new Error("Error en el servidor");

        // Al usar await, 'items' ya es directamente un PostDTO[]
        const data = await response.json(); 
        console.log(data);

        const hasMore = data.page.number < data.page.totalPages; // Si estamos en la última página, no hay más
        // Aseguramos que items sea SIEMPRE un array.
        // Si 'data' ya es el array, lo usamos. Si viene envuelto en 'content' o 'data', lo extraemos.
        // Si todo falla, devolvemos un array vacío [].
        let itemsArray: PostDTO[] = [];
        
        if (Array.isArray(data)) {
            itemsArray = data;
        } else if (data && Array.isArray(data.content)) {
            itemsArray = data.content;
        } else if (data && Array.isArray(data.data)) {
            itemsArray = data.data;
        }

        return { hasMore, items: itemsArray }
    } catch (error) {
        console.error(error);
        throw new Error("Error al cargar el feed");
    }
}

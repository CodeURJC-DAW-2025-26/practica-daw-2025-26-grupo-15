import type PostDTO from "~/dtos/PostDTO";
import type { UserDTO } from "~/dtos/UserDTO";


const API_POSTS_URL = "/api/v1/posts/";
const PAGE_SIZE = 25;

export async function getFeedForUser(page: number, user: UserDTO | null): Promise<{ hasMore: boolean; items: PostDTO[] }> {
    try {

        const response = await fetch(
            `${API_POSTS_URL}?page=${page}&size=${PAGE_SIZE}&currentUserId=${user ? user.id : ""}`
        );
        if (!response.ok) throw new Error("Error en el servidor");

        const data = await response.json(); 


        const hasMore = data.page.number < data.page.totalPages; 
        
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

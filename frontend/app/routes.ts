import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
	layout("routes/home.tsx", [
		index("routes/fyp.tsx"),
		route("users/:id", "routes/profile.tsx"),
		route("lists/:id", "routes/list-view.tsx"),
		route("lists/new", "routes/new-list.tsx"),
		route("lists/edit/:id", "routes/edit-list.tsx"),
		route("follow-requests", "routes/follow-request.tsx"),
		route("followers-following/followers", "routes/followers-following.tsx"),
	]),
] satisfies RouteConfig;

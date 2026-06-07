<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('user_event_participation', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id');
            $table->unsignedBigInteger('event_id');
            $table->enum('participation_status', ['pending', 'active', 'completed', 'cancelled'])->default('active');
            $table->integer('current_progress')->default(0);
            $table->integer('target_progress');
            $table->decimal('recharge_amount', 18, 2)->default(0);
            $table->decimal('spending_amount', 18, 2)->default(0);
            $table->integer('login_streak')->default(0);
            $table->integer('ranking_position')->nullable();
            $table->integer('tasks_completed')->default(0);
            $table->dateTime('joined_at')->useCurrent();
            $table->dateTime('completed_at')->nullable();
            $table->timestamps();

            $table->index('user_id');
            $table->index('event_id');
            $table->index('participation_status');
            $table->unique(['user_id', 'event_id']);
            $table->foreign('event_id')->references('id')->on('events')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('user_event_participation');
    }
};
